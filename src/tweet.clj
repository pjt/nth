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

(ns gropius.sramsay.nth 
  (:use clojure.contrib.java-utils)
  (:use clojure.contrib.command-line))

(load-file (str (System/getenv "NTH_HOME") "/src/auth.clj"))

(with-command-line
  *command-line-args*
  "Usage: tweet [-D username] message."
  [[direct? D? "Direct message" false]]
  (let [tweet (apply str (interpose " " *command-line-args*)) ; Awkward
        length (.length tweet)]                ; way to get a
    (if (<= length 140)                        ; string.
      (do
        (.updateStatus (get-auth) tweet)) ; Tweet!
      (do 
        (printf "\nSorry, you lost me at:\n\n%s\n" (apply str (take 140 tweet)))
        (printf "\nYou need to get rid of %d characters.\n" (- length 140))))))
