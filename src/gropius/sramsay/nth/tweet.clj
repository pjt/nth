;;; tweet.clj
;;;
;;; This file is part of nth
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Thu Mar 25 22:17:33 CDT 2010
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

(ns gropius.sramsay.nth.tweet
  (:use [gropius.sramsay.nth auth utils])
  (:use [clojure.contrib.str-utils :only (str-join)])
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(defn tweet
  [& args]
  (with-command-line
    args
    "Usage: tweet [-D username] message."
    [[direct D "Direct message" false] tweet-args]
    (let [tweet   (str-join " " tweet-args)
          length  (.length tweet)]
      (if (<= length 140)
        (do
          (.updateStatus (get-twitter-object) tweet)) ; Tweet!
        (do 
          (printlnf "\nSorry, you lost me at:\n\n%s\n" (apply str (take 140 tweet)))
          (printlnf "\nYou need to get rid of %d characters.\n" (- length 140)))))))
