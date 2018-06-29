#!/usr/bin/python3

from flask import Flask, render_template, request
import requests
import time
import sqlite3
import os
import json
import random

app = Flask(__name__)

if not os.path.isfile("database.db") :
    conn = sqlite3.connect("database.db")
    cursor = conn.cursor()
    cursor.execute("""
    Create Table Musics (
        idx INTEGER PRIMARY KEY AUTOINCREMENT,
        genre varchar(255),
        artist varchar(255),
        playback int(255),
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
    );
    """)
    cursor.execute("""
        Create Table Likes (
        idx INTEGER PRIMARY KEY AUTOINCREMENT,
        imei int(11) default 0,
        musicIdx int(11) default 0
    );
    """)
    cursor.execute("""
        Create Table Walhalla (
        idx INTEGER PRIMARY KEY AUTOINCREMENT,
        imei int(11) default 0,
        musicIdx int(11) default 0
    );
    """)
    cursor.execute("""
        Create Table Magazine (
        idx INTEGER PRIMARY KEY AUTOINCREMENT,
        title varchar(255) default "",
        url varchar(255) default "",
        content text default ""
    );
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
    cursor.execute("Select * from Musics")
    return json.dumps(cursor.fetchall())

@app.route("/like/<idx>")
def like(idx) :
    imei = request.args['imei']
    musicIdx = idx
    try :
        imei = int(imei)
        musicIdx = int(musicIdx)
    except :
        return "error"
    cursor.execute("Select idx from Likes where imei=? and musicIdx=?", (imei, musicIdx))
    if cursor.fetchall() :
        return "dislike"
        cursor.execute("Delete from Likes where imei=? and musicIdx=?", (imei, musicIdx))
    cursor.execute("Insert into Likes(imei, musicIdx)values(?, ?)", (imei, musicIdx))
    conn.commit()
    return "like"

@app.route("/mainPlayList")
def mainPlayList() :
    cursor.execute("Select * from Musics")
    lst = cursor.fetchall()
    random.shuffle(lst)
    return json.dumps(lst[0])

@app.route("/likeList")
def likeList() :
    imei = request.args['imei']
    try :
        imei = int(imei)
    except :
        return "error"
    cursor.execute("Select musicIdx from Likes where imei=?", (imei,))
    lst = []
    for row in cursor.fetchall() :
        cursor.execute("Select * from Musics where idx=?", (row[0],))
        lst.append(cursor.fetchone())
    random.shuffle(lst)
    return json.dumps(lst[0])

@app.route("/Walhalla")
def Walhalla() :
    imei = request.args['imei']
    try :
        imei = int(imei)
    except :
        return "error"
    cursor.execute("Select * from Musics where playback > 1000")
    lst = cursor.fetchall()
    random.shuffle(lst)
    cursor.execute("Insert into Walhalla(imei, musicIdx)values(?, ?)", (imei, lst[0]['idx']))
    conn.commit()
    return json.dumps(lst[0])

@app.route("/shuffle/<category>")
def shuffle(category) :
    cursor.execute("Select * from Musics where genre=?", (category,))
    lst = cursor.fetchall()
    random.shuffle(lst)
    return json.dumps(lst[0])

@app.route("/crawlTag")
def crawlTag() :
    f = open("./crawlTag", "r")
    lst = f.read().split("\n")
    f.close()
    return json.dumps(lst)

@app.route("/write")
def write() :
    return render_template("write.html")

@app.route("/write", methods=["POST"])
def writeUpdate() :
    title = request.form['title']
    content = request.form['content']
    url = request.form['url']
    cursor.execute("Insert into Magazine(title, content, url)values(?, ?, ?)", (title, content, url))
    conn.commit()
    return "Success"

@app.route("/article")
def article() :
    cursor.execute("Select * from Magazine where 1")
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
                    cursor.execute("Select idx from Musics where track=?", (track,))
                    if len(cursor.fetchall()) > 0 :
                        continue
                    cursor.execute("Insert into Musics(genre, artist, playback, track, bgImage, title)values(?, ?, ?, ?, ?, ?)", (genre, artist, playback, track, bgImage, title))
                    conn.commit()        
            except :
                pass

    return ""

if __name__ == "__main__" :
    app.debug = True
    app.run(host="0.0.0.0")
