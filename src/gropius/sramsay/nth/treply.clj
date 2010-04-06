;;; treply.clj
;;;
;;; This file is part of nth
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Tue 06 Apr 2010 10:51 CDT
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

(ns gropius.sramsay.nth.treply
  (:use [gropius.sramsay.nth auth inbox utils])
  (:use [clojure.contrib.str-utils :only (str-join)])
  (:use clojure.contrib.command-line))

(defn treply
  [& args]
  (with-command-line
    args
    "Usage: treply msg-number reply-text\n(If no @user in tweet, it will be added.)"
    [msg-text]
    (let [[msg-num & text] msg-text
          msg       (get-inbox-file msg-num)
          msg-id    (:id msg)
          tweet     (str-join " " text)
          tweet     (if (re-seq #"@\S+" tweet) 
                      tweet
                      (format "@%s %s" (:user msg) tweet))]
      (assert-length tweet)
      (if-not msg-id
        (do
          (.println *err* (format "No message number %s found." msg-num))
          (System/exit 1))
        (.updateStatus (get-twitter-object) tweet msg-id))))) ; Tweet-in-reply!
