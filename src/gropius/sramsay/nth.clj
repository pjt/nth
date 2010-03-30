;;; nth.clj
;;;
;;; This file is part of nth
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Mon 29 Mar 2010 21:36 CDT
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
  (:gen-class))

; Command broker for nth: expects command name as first argument, finds command
; as an nth namespace, calls its named function with remaining arguments
(defn -main 
  [& args]
  (let [nm      (first args)
        cmd-ns  (symbol (format "gropius.sramsay.nth.%s" nm))]
    (try 
      (require cmd-ns)
      (apply (ns-resolve cmd-ns (symbol nm)) (rest args))
      (catch java.io.FileNotFoundException _
        (.println *err* (format "Nth command `%s` not found." nm))
        (System/exit 1))
      (catch java.lang.NullPointerException _
        (.println *err* 
          (format "Error calling `%s` -- Clojure source not as expected." nm))
        (System/exit 1)))))
