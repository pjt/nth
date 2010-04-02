;;; tscan.clj
;;;
;;; This file is part of nth
;;;
;;; Get a full view of a numbered tweet.
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

(ns gropius.sramsay.nth.tscan
  (:import 
     (java.io File))
  (:use [gropius.sramsay.nth inbox utils])
  (:use clojure.contrib.command-line))

(defn digest-view [update]
  "Write updates to screen (format as: num time nick tweet)"
  (printf "%4d %s %-15s %s\n",
          (:number update)
          (re-find #"[0-9]{2}:[0-9]{2}" (:created_at update))
          (:user update)
          (apply str (take 52 (:text update))))
  (.flush *out*)) 

(defn display-timeline [timeline]
  "Write new updates to screen or signal no new messages."
  (doseq [update (vals timeline)]
    (digest-view update)))

(defn tscan
  [& args]
  (with-command-line
    args
    "Usage: tscan [-s query]"
    [[version? v? "Version 1.0"]] 
    (let [current-timeline (get-inbox)]
      (if (inbox-is-empty?)
        (println "tscan: no updates in inbox")
        (display-timeline current-timeline)))))
