;;; twinc.clj
;;;
;;; This file is part of nth
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
;;; 
;;; Retrieves last twenty status updates in timeline
;;;
;;; Written and Maintained by Stephen Ramsay
;;;
;;; Last Modified: Fri Mar 19 10:12:04 CDT 2010

(ns gropius.sramsay.nth
  (:import 
     (twitter4j Twitter)
     (java.text SimpleDateFormat))
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(defstruct timeline :number :created_at :user :source :text)

;; This is how you do a counter with STM.
;; No, I don't really understand it.
;; Code now, ask questions later.
(def counter (let [count (ref 0)] #(dosync (alter count inc))))

(load-file (str (System/getenv "NTH_HOME") "/src/auth.clj"))

(defn make-timeline-struct [updates]
  "Struct corresponds to the Status interface in twitter4j"
  (struct timeline
          (counter)
          (.getCreatedAt updates)
          (.getScreenName (.getUser updates))
          (.getSource updates)
          (.getText updates)))

(with-command-line
  *command-line-args*
  "Usage: inc [-s query]"
  [[search? s? "Search query"]] ; unimplemented
  (let [timeline (.getFriendsTimeline (get-auth))
        updates (map #(make-timeline-struct %) timeline)]
    (doall (for [status updates] 
             ; format as "num time nick tweet"
             (printf "%4d %s %-15s %s\n",
                     (:number status)
                     (.format (new SimpleDateFormat "HH:mm") (:created_at status))
                     (:user status)
                     (apply str (take 52 (:text status))))))))
