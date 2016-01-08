Game-Updater

An updater running with zip releases.
Zip-Builder is the application which search diff between built zip releases and the uncompressed files (which are in a specificate folder) -> checksum

Distant Client will also check releases (atm 1.zip 2.zip etc.., can make it funniest with md5 or idk xD) and download/install only the files that the client doesn't have atm. No duplicated files with dat way because at each zip building (at each release), modified files are removed in the old releases.

For Example, A.zip contains a.xml and b.xml. Im zipping a new realease changing a.xml then B.zip is created and changed a.xml is added, and removed in A.zip. In some: A.zip -> b.xml and B.zip -> a.xml

With that way, new clients which start download being at 0 release, meaning first download, won't download 2 times the same file, it's why this way is the best one to make your updater, it's faster. File per File download is a possible way, anyway this takes too much time creating too many connections, slowly. Zip way is better, i tested it, it was horrible.
