;;; tshow.clj
;;;
;;; This file is part of nth
;;;
;;; Get a full view of a numbered tweet.
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Thu Apr 01 22:38:45 CDT 2010
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

(ns gropius.sramsay.nth.tshow
  (:import
     (java.io File))
  (:use [gropius.sramsay.nth utils inbox])
  (:use clojure.contrib.command-line))

(defn full-view [update]
  "Write a full view of the specified update to stdout"
  (let [tweet (peek update)]
    (printf "(Update inbox: %s)\n\n", (:number tweet))
    (println (:text tweet))
    (printf "\n[%s, %s, via %s]\n",
            (:user tweet),
            (:created_at tweet),
            (peek (re-find #">(.*)<" (:source tweet))))))

(defn tshow
  [& args]
  (with-command-line
    args
    "Usage: inc [-s query]"
    [[search s "Search query"] nums] ; unimplemented
    (let [num (first nums)]
      (full-view (get-inbox-file num)))))
