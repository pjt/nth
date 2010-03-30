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

(ns gropius.sramsay.nth.twinc
  (:import 
     (twitter4j Status Twitter)
     (java.text SimpleDateFormat))
  (:use [gropius.sramsay.nth.auth     :only (get-twitter-object)])
  (:use [clojure.contrib.duck-streams :only (spit)])
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(defstruct timeline :number :id :created-at :user :source :text)

(defn- indexed-from
  "Produces indexed sequence (items as [idx x]), with indices starting 
  from n. (Cf. clojure.contrib.seq-utils/indexed.)"
  [n coll]
  (map vector (iterate inc n) coll))

(def nth-home  (System/getenv "NTH_HOME"))
(def user-home (System/getenv "HOME"))

(defn make-timeline-struct [n #^Status update]
  "Struct corresponds to the Status interface in twitter4j"
  (struct timeline
          n
          (.getId update)
          (.getCreatedAt update)
          (.getScreenName (.getUser update))
          (.getSource update)
          (.getText update)))

(defn twinc
  [& args]
  (with-command-line args
    "Usage: inc [-s query]"
    [[search s "Search query"]] ; unimplemented
    (let [timeline  (.getFriendsTimeline (get-twitter-object))
          updates   (map (partial apply make-timeline-struct) 
                          (indexed-from 1 timeline))]
      (doseq [status updates] 
        (spit (str user-home "/Twitter/inbox/" (:number status)) (:text status))
        ; format as "num time nick tweet"
        (printf "%4d %s %-15s %s\n",
               (:number status)
               (.format (new SimpleDateFormat "HH:mm") (:created-at status))
               (:user status)
               (apply str (take 52 (:text status))))
        (.flush *out*)))))
