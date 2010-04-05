;;; utils.clj
;;;
;;; Miscellaneous utility functions.
;;;
;;; This file is part of nth
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Thu Apr 01 22:03:23 CDT 2010
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

(ns gropius.sramsay.nth.utils
  (:import
     (java.io File)
     (java.io FileWriter)))

(def nth-home  (System/getenv "NTH_HOME"))
(def user-home (System/getenv "HOME"))
(def nth-dir   (str user-home "/Twitter")) ; boxes, etc.
(def inbox-dir (str nth-dir "/inbox"))     ;

(defn numeric-comparator []
  "Comparator function for sorting numbers"
  ; Is this really necessary?
  (comparator (fn [a b] (if (> (count a) (count b)) nil (.compareTo b a)))))

(defn indexed-from
  "Produces indexed sequence (items as [idx x]), with indices starting 
  from n. (Cf. clojure.contrib.seq-utils/indexed.)"
  [n coll]
  (map vector (iterate inc n) coll))

(defn read-form [filename]
  "Read a Clojure form from a file."
  (try
    (let [form (read-string (slurp filename))]
      form)
    (catch Exception e (.printStackTrace e))))

(defn write-form [form filename]
  "Write a Clojure form to a file."
  (binding [*out* (FileWriter. filename)]
    (prn form)))

(defn printlnf
  "Short for (println (format ...)). Printf often requires flushing; this doesn't."
  [& args]
  (println (apply format args)))

(defn digest-view [update]
  "Write updates to screen (format as: num time nick tweet)"
  (printlnf "%4d %s %-15s %s",
          (:number update)
          (re-find #"[0-9]{2}:[0-9]{2}" (:created_at update))
          (:user update)
          (apply str (take 52 (:text update)))))

(defn display-timeline [timeline]
  "Write new updates to screen or signal no new messages."
  (doseq [update (sort-by :number > (vals timeline))]
    (digest-view update)))

