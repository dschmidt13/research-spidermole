Spidermole (for Research)
===

*A project to help anyone comb through scientific literature. It crawls archives like a spider, and it digs through the data like a mole.*

**Please note:** This project's theoretical goals and practical capabilities are
slightly different at the moment. The inspiration came from my (the author's)
desire to search for graduate programs in Systems Biology by finding researchers
(and thereby institutions) whose work most strongly interested and/or resonated
with me (whether by topic, approach, result, etc., or ideally some combination).
However, the project was written in a huge hurry (roughly a week), so while it
is *fairly* architecturally stable, some of its components are highly specific
to this purpose -- and only one version of this purpose.

Eventually, the hope is to transition to more general research mining tasks for
more general purposes: ML, NLP, clustering, cross-referencing, mapping/graphing,
and other buzzwords.

Currently, this is what it can do (aka "Features"):
---

 - **Responsibly** download research metadata from
 	[bioRxiv's API](https://api.biorxiv.org)
 - Present a (filterable) sequence of individual research pieces (as titles with
 	abstracts) for research categorized under "Systems Biology" (Sorry - if you
 	want something else, feel free to adjust the source for now)
 - Allow the user to "vote" Tinder-style (yes or no/right or left) on each
 	research item
 - Compute and display some basic summary information about "Yes" votes
 - These data are all stored on the local machine under a
 	specifically-configured (CouchDB)[https://couchdb.apache.org/] instance (more on that below)

Authors
---

 - David Schmidt

Dependencies (Installed Locally)
---

 - Java SE 11+
 - Gradle
 - git
 - CouchDB (see **CouchDB Configuration** below)

CouchDB Configuration
---

**Please note:** This CouchDB configuration is far less than secure. If you
intend to (or already do) use CouchDB for other purposes, please modify this
project's source code to suit your needs. Otherwise, be sure *not* to expose
CouchDB ports outside localhost unless you have a strong need and really trust
your network. More dynamic configuration code may be forthcoming, but as this
project was written in some haste and it wasn't a top proiority, it didn't get
done. So for now, all database properties, including the username and password
are currently hardcoded. My apologies!

1. [Install on localhost](https://docs.couchdb.org/en/stable/install/index.html)
2. For username and password, use **admin** for both (feel free to cringe)
3. Expose CouchDB on the default port, 5984 (just a tip for existing installs)
4. Create a database named **spidermole**

To configure indexing (which *greatly* speeds up the app), add the following
"Mango Indexes" under the Design Documents section of your new **spidermole**
database (these may be pasted in directly above the "Create index" button):

 - `{ "index": { "fields": [ "type", "yesVotes" ] }, "name": "yes-votes-index", "type": "json" }`
 - `{ "index": { "fields": [ "type", "doi" ] }, "name": "doi-index", "type": "json" }`
 - `{ "index": { "fields": [ "type", "category" ] }, "name": "category-index", "type": "json" }`
 - `{ "index": { "fields": [ "type", "category", "yesVotes", "noVotes" ] }, "name": "all-votes-index", "type": "json" }`

Running the Project
---
To run the project, ensure that CouchDB is properly configured and running as
described, and then run the following commands in a terminal:

    cd .../a_code_folder/
    git clone https://github.com/dschmidt13/research-spidermole.git research-spidermole
    cd research-spidermole
    gradle run

Downloading Research Metadata (with Examples)
---

Downloaded research item metadata is stored locally in CouchDB, so the app may be used offline after an initial download step is performed.

**This app does its best to download responsibly; that is, without overwhelming servers. Be aware that downloading any significant amount of data will likely take several hours.**

### The following download servers/syntaxes are supported
*In order to go outside this list, provide a custom implementation of the *
`CrawlCorrespondent` *interface and register it by domain in the *
`CrawlService`*.*

##### bioRxiv API: http://api.biorxiv.org/
Only the format:

    https://api.biorxiv.org/details/biorxiv/[yyyy-MM-dd]/[yyyy-MM-dd]/[cursor]

is well-tested. The option using an integer for the **interval** value *might*
work, but the server might not like it as well as the documentation suggests.
And it might not paginate correctly (no promises).

Example to download EVERYTHING (only do this once!):

    https://api.biorxiv.org/details/biorxiv/1970-01-01/2030-01-01/0

Debugging the Project
---
To debug the project in [Eclipse](https://www.eclipse.org/):

1. Import it into Eclipse and set a breakpoint somewhere in the code.
2. *Run* the provided **Run Spidermole (Remote Debug Required)** launcher using the Eclipse *Run* menu.
3. Wait for the message "Listening for transport dt_socket at address: 9099" in the Console.
4. *Debug* the provided **Debug Spidermole (Remote)** launcher to connect to the application in the Eclipse debugger.

Alternately, if using a different debugger, launch like so:

    gradle run -DDEBUG=true

and then connect to the waiting application, using the remote Java debugger of
your choice, via Port 9099.

Troubleshooting
---
##### Unsupported class file major version 57
If this shows up in the Console or Problems view while building in Eclipse,
first check that you can build and run the project without modifications from
the command line outside Eclipse. If so, this is likely an issue caused by
Buildship, the Eclipse plugin that manages Gradle projects. It occurs when
Eclipse is itself executing in a later JVM than Java SE 11, such as Java SE 13.
To resolve it, close Eclipse, then go to your Eclipse installation directory and
modify the eclipse.ini file to point to the bin folder of a valid Java SE 11
installation, a la:

    ...
    -vm C:/Program Files/Java/openjdk-11/bin
    ...

License: Free-For-All (FFA)
---
This code and codebase may be used and propagated with no restrictions,
constraints, or stipulations. You may even lie and tell people it's your code,
although if they find this repository it's your problem (and may that happen
swiftly if you do)!
