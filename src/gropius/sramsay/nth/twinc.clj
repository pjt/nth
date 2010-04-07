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

(ns gropius.sramsay.nth.twinc
  (:import 
     (twitter4j Paging Status Twitter)
     (java.io File)
     (java.io FileWriter)
     (java.text SimpleDateFormat))
  (:use clojure.set)
  (:use [gropius.sramsay.nth auth inbox utils])
  (:use [clojure.contrib.duck-streams :only (spit)])
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(defstruct timeline :number :id :created_at :user :source :text)

(defn timeline-struct [n #^Status update]
  "Struct corresponds to the Status interface in twitter4j"
  (struct timeline
          n
          (.getId update)
          (.toString (.getCreatedAt update))
          (.getScreenName (.getUser update))
          (.getSource update)
          (.getText update)))

(defn get-timeline []
  "Retrieve last 20 updates"
  (let [recent      (most-recent-in-inbox)
        old-to-new  (reverse (.getFriendsTimeline (get-twitter-object) 
                                (Paging. (get recent :id 1))))
        indexed     (indexed-from (inc (get recent :number 0)) old-to-new)
        timeline    (map (partial apply timeline-struct) indexed)]
    (into {} (for [update timeline] [(:id update) update]))))

(defn write-timeline [timeline]
  "Write structs as numbered files in inbox"
  (doseq [update (vals timeline)]
   (write-form update (str inbox-dir "/" (:number update)))))

(defn diff-new-updates [new-timeline old-timeline]
  "Compare id keys in most recent timeline to what's in the inbox"
  (let [new-ids (into #{} (keys new-timeline))
        old-ids (into #{} (keys old-timeline))
        current-ids (clojure.set/difference
                      (clojure.set/union new-ids old-ids)
                      (clojure.set/intersection new-ids old-ids))]
    (select-keys new-timeline current-ids)))

(defn twinc
  [& args]
  (with-command-line
    args
    "Usage: inc [-s query]"
    [[search s "Search query"]] ; unimplemented
    (let [current-timeline (get-timeline)
          most-recent-updates (diff-new-updates current-timeline (get-inbox))]
      (display-timeline most-recent-updates)
      (write-timeline most-recent-updates))))
