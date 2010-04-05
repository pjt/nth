;;; inbox.clj
;;;
;;; Functions related to the manipulation of the inbox.
;;;
;;; This file is part of nth
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Thu Apr 01 22:01:18 CDT 2010
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

(ns gropius.sramsay.nth.inbox
  (:use gropius.sramsay.nth.utils)
  (:import 
     (java.io File)))

(defn inbox-files []
  "Sequence of inbox files"
  (drop 1 (file-seq (File. (str inbox-dir "/")))))

(defn get-inbox []
  "Returns map from timeline id to timeline struct."
  (into {}
    (for [f (inbox-files)]
      (let [update (read-form (.getPath f))]
        [(:id update) update]))))

(defn get-inbox-file [num]
  "Returns the corresponding tweet struct"
  (read-form (.getPath (File. (str inbox-dir "/" num)))))
  
(defn inbox-is-empty? []
  "Check $HOME/Twitter/inbox for files."
  (empty? (inbox-files)))

(defn next-inbox-num
  "Returns the highest number in inbox plus one."
  []
  (let [filenums (map #(Integer/parseInt (.getName %)) (inbox-files))]
    (inc (or (first (sort-by identity > filenums)) 0))))

(def new-inbox-num
  (let [inbox-filenames (inbox-files)
        inbox-size (ref (count inbox-filenames))]
    #(dosync (alter inbox-size inc))))
