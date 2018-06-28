#!/usr/bin/python3

from flask import Flask, render_template
import requests
import time
import sqlite3
import os
import json

app = Flask(__name__)

if not os.path.isfile("database.db") :
    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()
    cursor.execute("""
    Create Table Musics (
        idx INTEGER PRIMARY KEY AUTOINCREMENT,
        genre varchar(255),
        artist varchar(255),
        playback varchar(255),
        track int(255),
        bgImage varchar(255),
        title varchar(255),
        feature_1 int(11) DEFAULT 0,
        feature_2 int(11) DEFAULT 0,
        feature_3 int(11) DEFAULT 0,
        feature_4 int(11) DEFAULT 0,
        feature_5 int(11) DEFAULT 0,
        feature_6 int(11) DEFAULT 0,
        feature_7 int(11) DEFAULT 0,
        feature_8 int(11) DEFAULT 0,
        feature_9 int(11) DEFAULT 0,
        feature_10 int(11) DEFAULT 0,
        feature_11 int(11) DEFAULT 0,
        feature_12 int(11) DEFAULT 0
    )
    """)
    cursor.close()
    conn.commit()
    conn.close()

conn = sqlite3.connect("database.db", check_same_thread=False)
cursor = conn.cursor()

@app.route("/")
def index() :
    return render_template('index.html', host="http://localhost:5000/")

@app.route("/download")
def download() :
    # f = open("crawlTag", "r")
    # tags = f.read().split("\n")
    # f.close()
    cursor.execute("Select * from Musics")
    return json.dumps(cursor.fetchall())

@app.route("/crawl")
def crawl() :
    global conn
    global cursor

    f = open("crawlTag", "r")
    tags = f.read().split("\n")
    f.close()

    for tag in tags :
        for offset in ["0", "200"] :
            url = "https://api-v2.soundcloud.com/charts?kind=trending&genre=soundcloud%3Agenres%3A" + tag +"&high_tier_only=false&client_id=unnFdubicpq7RVFFsQucZzduDPQTaCYy&limit=200&offset=" + offset + "&linked_partitioning=1&app_version=1530190612&app_locale=en"
            req = requests.get(url)
            f = open("./crawled/" + str(int(time.time())) + "_" + tag, "wb")
            f.write(req.content)
            f.close()
            try :
                for collection in json.loads(req.content.decode())['collection'] :
                    genre = tag
                    bgImage = collection['track']['artwork_url']
                    title = collection['track']['title']
                    artist = "Anonymous"
                    try :
                        artist = collection['track']['publisher_metadata']['artist']
                    except :
                        pass
                    track = collection['track']['id']
                    playback = collection['track']['playback_count']
                    cursor.execute("Insert into Musics(genre, artist, playback, track, bgImage, title)values(?, ?, ?, ?, ?, ?)", (genre, artist, playback, track, bgImage, title))
                    conn.commit()        
            except :
                pass

    return ""

if __name__ == "__main__" :
    app.debug = True
    app.run(host="0.0.0.0")
