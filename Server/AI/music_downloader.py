import numpy as np
import pandas as pd
import wget
from six.moves import urllib
from pprint import pprint as p
import urllib.request
import requests

json_url='http://bughunting.net:5000/download'
data=urllib.request.urlopen(json_url).read()

#playback is null DEL
for i in range(len(data)):
    if(data[i][3]=="empty"):
        del data[i]
#pd setting
pd_data=pd.DataFrame(data)
pd_data.columns = ['index','genre','artist','playback','track_num','bgimage','title','ZeroCrossing Rate','Energy','EntropyofEnergy','SpectralCentroid','SpectralSpread','SpectralEntropy','SpectralFlux','Spectral Rolloff','MFCCs','ChromaVector','ChromaDeviation','f12']

#hip list filtering
main_data=pd_data[pd_data.playback<20000]

#np setting
np_data=pd_data.as_matrix()

# music file download at soundcloud
for d in main_data:
    track_num=str(d[4]) 
    url='http://api.soundcloud.com/tracks/'+track_num+'/download?client_id=unnFdubicpq7RVFFsQucZzduDPQTaCYy'
    path='~/Desktop/2018_openHack/music/'+str(d[2])+'_'+str(d[6])+'_'+str(d[4])+'.mp3'
    urllib.request.urlretrieve(url, path) 