# nth

> I think the major good idea in Unix was its clean and simple interface: open, close, read, and write. -- Ken Thompson 

Nth is a command-line twitter client designed with the UNIX philosophy in mind.  It aims to be a close equivalent to nmh (the old, "Message Handler" email client designed by the evil geniuses at the RAND Corporation back in the day).

I would say more, but it's in a dangerous alpha stage and will almost certainly cause you great pain.  Also, the author is using this code as an opportunity to learn Clojure.  That means not only pain, but ugliness.

Here are a few tips for those too stupid to Stop Right Here.

1. Nth is written in Clojure.  It expects to find java in the PATH and clojure.jar and clojure-contrib.jar in the CLASSPATH (it's designed to work with version 1.1.0).

2. Nth uses the Twitter4j library, and seems only to work properly under the latest version (twitter4j-core-2.1.1-SNAPSHOT.jar).

3. Nth can run without it, but it's way more fun with [nailgun](http://martiansoftware.com/nailgun/index.html).  That means having the nailgun jar in the CLASSPATH and ng in the PATH.

4. Set NTH_HOME to the nth root directory somewhere and make a directory called Twitter underneath HOME.

5. Implemented commands (tweet, twinc, tscan, and tshow) are in NTH_HOME/bin.

## License

Copyright (C) 2010 by Stephen Ramsay

nth is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3, or (at your option) any later version.

nth is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with nth; see the file COPYING.  If not see <http://www.gnu.org/licenses/>
