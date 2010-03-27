;;; auth.clj
;;;
;;; This file is part of nth
;;;
;;; Perform various miracles involving OAuth authentication.
;;; 
;;; Looks for a serialized oauth object.  If it finds one, returns
;;; an object of type Twitter to the caller.  If it doesn't find one,
;;; it sends the user off to twitter for a pin, accepts the pin,
;;; serializes the object, and returns Twitter to the caller (I think).
;;;
;;; Fails catastrophically if you screw up the pin.  Checks that the pin
;;; is seven digits (which might not be carved in stone).
;;;
;;; Written and maintained by Stephen Ramsay <sramsay.unl@gmail.com>
;;;
;;; Last Modified: Fri Mar 26 23:38:56 CDT 2010
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

(ns gropius.sramsay.nth.auth
  (:import
     (twitter4j Twitter)
     (twitter4j TwitterFactory) 
     (twitter4j.http RequestToken)
     (twitter4j.http AccessToken) 

     (java.io File)
     (java.io InputStream)
     (java.io BufferedReader)
     (java.io FileOutputStream)
     (java.io FileInputStream)
     (java.io ObjectOutputStream)
     (java.io ObjectInputStream)
     (java.io InputStreamReader)))

(System/setProperty "twitter4j.oauth.consumerKey" "XqQY44s716uCi4dRtAa80Q")
(System/setProperty "twitter4j.oauth.consumerSecret" "FTQPAnR2LxvacKEqjpj2NF688bOc8ggsIg0c0D7Rc5s")

(def twitter (.getInstance (new TwitterFactory)))

;;
;; Magical serialization functions
;;

(defn serialize [object filename]
  (let [os (new FileOutputStream filename)]
    (with-open [oo (new ObjectOutputStream os)]
      (.writeObject oo object))))

(defn deserialize [filename]
  (let [is (new FileInputStream filename)]
    (with-open [oi (new ObjectInputStream is)]
      (cast twitter4j.http.AccessToken (.readObject oi)))))

;;
;; Sort out authentication
;;

(defn get-twitter-object []
  (let [oauth-ser (str (System/getenv "NTH_HOME") "/oauth.ser")]
  (if (.exists (new File oauth-ser)) ; If we have a serialized OAuth object
    (.setOAuthAccessToken twitter (deserialize oauth-ser)) ; Go with that.
    (do ; Otherwise, give the user a URL and have them give you the pin
      (def request_token (.getOAuthRequestToken twitter))
      (println "Open the following URL to grant this program access to your account.")
      (println (.getAuthorizationURL request_token))
      (println "After doing that, you may enter your PIN: ")
      (def pin (read-line))
      (if (re-matches #"[0-9]{7}" pin) ; 7-digit number, right?
        (do
          (serialize (.getOAuthAccessToken twitter request_token pin) oauth-ser))
        (do
          (println "That pin doesn't seem to be correct.") ; Should do something
          (System/exit 0)))))                              ; nicer here
  twitter)) ; Return the all-powerful twitter object
