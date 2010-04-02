;;; twinc.clj
;;;
;;; This file is part of nth
;;;
;;; Checks the main timeline.  If it finds new updates, simultaneously
;;; writes them (in digest form) to the screen, and to $HOME/Twitter/inbox.
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Wed Mar 31 22:42:25 CDT 2010
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
  (:use [clojure.contrib.duck-streams :only (spit)])
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(def nth-home  (System/getenv "NTH_HOME"))
(def user-home (System/getenv "HOME"))
(def nth-dir   (str user-home "/Twitter")) ; boxes, etc.
(def inbox-dir (str nth-dir "/inbox"))     ;

(load-file (str nth-home "/src/auth.clj"))
(load-file (str nth-home "/src/inbox.clj"))

(defstruct timeline :number :created_at :user :source :text)

(defn timeline-struct [updates]
  "Struct corresponds to the Status interface in twitter4j"
  (struct timeline
          (new-inbox-num)
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
  "Write new updates to screen or signal no new messages."
  (doall 
    (if (empty? timeline)
      (println "twinc: no updates to twincorporate")
      (map #(digest-view %) (vals timeline)))))

(defn diff-new-updates [new-timeline old-timeline]
  "Compare id keys in most recent timeline to what's in the inbox"
  (let [new-ids (into #{} (keys new-timeline))
        old-ids (into #{} (keys old-timeline))
        current-ids (lazy-cat (clojure.set/difference new-ids old-ids) 
                              (clojure.set/difference old-ids new-ids))]
    (select-keys new-timeline current-ids)))

(with-command-line
  *command-line-args*
  "Usage: inc [-s query]"
  [[search? s? "Search query"]] ; unimplemented
  (let [current-timeline (get-timeline)
        most-recent-updates (diff-new-updates current-timeline (get-inbox))]
    (if (inbox-is-empty?)
      (do
        (display-timeline current-timeline)
        (write-timeline current-timeline))
      (do 
        (display-timeline most-recent-updates)
        (write-timeline most-recent-updates)))))
