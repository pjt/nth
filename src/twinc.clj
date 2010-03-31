;;; twinc.clj
;;;
;;; This file is part of nth
;;;
;;; Retrieves last twenty status updates in timeline
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Thu Mar 25 22:17:12 CDT 2010
;;;
;;; Copyright (c) 2010 Stephen Ramsay
;;;
;;; nth is free software; you can redistribute it and/or modify
;;; it under the terms of the GNU General Public License as published by
;;; the Free Software Foundation; either version 3, or (at your option) any
;;; later version.
;;;
;;; nth is distributed in the hope that it will be useful, but
;;; WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
;;; or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
;;; for more details.
;;;
;;; You should have received a copy of the GNU General Public License
;;; along with nth; see the file COPYING.  If not see
;;; <http://www.gnu.org/licenses/>.

(ns gropius.sramsay.nth
  (:import 
     (twitter4j Twitter)
     (java.io File)
     (java.io FileWriter)
     (java.text SimpleDateFormat))
  (:use clojure.set)
  (:use [clojure.contrib.duck-streams :only (spit slurp*)])
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(def nth-home  (System/getenv "NTH_HOME"))
(def user-home (System/getenv "HOME"))
(def nth-dir   (str user-home "/Twitter")) ; boxes, etc.
(def inbox-dir (str nth-dir "/inbox"))

(load-file (str nth-home "/src/auth.clj"))

(defstruct timeline :number :created_at :user :source :text)

;; This is how you do a counter with STM.
;; No, I don't really understand it.
;; Code now, ask questions later.
(def counter
  (let [count (ref 0)]
    #(dosync (alter count inc))))

(defn write-form [form filename]
    (binding [*out* (FileWriter. filename)]
      (prn form)))

(defn read-form [filename]
  (try
    (let [form (read-string (slurp filename))]
      form)
    (catch Exception e (.printStackTrace e))))

(defn inbox-files []
  "Sequence of inbox files"
  (drop 1 (file-seq (File. (str inbox-dir "/")))))

(defn inbox-is-empty? []
  (empty? (inbox-files)))

(defn get-inbox []
  "Sequence containing time-line structs"
  (map #(read-form (.getPath %)) (inbox-files)))

(defn timeline-struct [updates]
  "Struct corresponds to the Status interface in twitter4j"
  (struct timeline
          (counter)
          (.toString (.getCreatedAt updates))
          (.getScreenName (.getUser updates))
          (.getSource updates)
          (.getText updates)))

(defn get-timeline []
  "Retrieve last 20 updates"
  (let [timeline (.getFriendsTimeline (get-twitter-object))]
    (zipmap (map #(keyword (str (.getId %))) timeline) (map #(timeline-struct %) timeline))))

(defn write-timeline [timeline]
  "Write structs as numbered files in inbox"
  (doall
    (for [update timeline]
      (write-form update (str inbox-dir "/" (:number (val update)))))))

(defn digest-view [update]
  "Write updates to screen (format as: num time nick tweet)"
  (printf "%4d %s %-15s %s\n",
          (:number update)
          (re-find #"[0-9]{2}:[0-9]{2}" (:created_at update))
          (:user update)
          (apply str (take 52 (:text update)))))

(defn display-timeline [timeline & ids]
  (doall 
    (map #(digest-view %) (vals timeline))))
;    (let [ids (first ids)
;          new-updates (loop [result []
;                             id (first ids)]
;                        (if (nil? id)
;                          result
;                          (recur 
;                            (doall
;                              (for [update timeline]
;                                (when (== id (:id update))
;                                  (do
;                                  (conj result update))))) (rest ids))))]) 
;      (doall
;      (if (empty? new-updates)
;        (println "twinc: no updates to twincorporate")
;        (do
;          (for [update new-updates]
;            (digest-view update))))))

(defn new-updates [new-timeline old-timeline]
  (let [new-ids (into #{} (map #(:id %) new-timeline))
        old-ids (into #{} (map #(:id %) old-timeline))]
    (lazy-cat (clojure.set/difference new-ids old-ids) 
              (clojure.set/difference old-ids new-ids)))) 

(with-command-line
  *command-line-args*
  "Usage: inc [-s query]"
  [[search? s? "Search query"]] ; unimplemented
  ;(let [new-timeline (get-timeline)
  (let [new-timeline (get-timeline)]
  ;      most-recent-ids (new-updates new-timeline (get-inbox))]
    (when (inbox-is-empty?)
      (do
        (display-timeline new-timeline)
        (write-timeline new-timeline)))))
    ;(do 
    ;    (display-timeline new-timeline most-recent-ids))))
