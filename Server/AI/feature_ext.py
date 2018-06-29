import glob
import os 

#get Path of all music
music_list=glob.glob('/home/scio/Desktop/2018_openHack/pyAudioAnalysis/*.wav')

featuresOfmusic=list()

for music in music_list:
    #Feature Ext
    os.system('cd /home/scio/Desktop/2018_openHack/pyAudioAnalysis')
    os.system('python audioAnalysis.py featureExtractionFile -i test.wav -mw 1.0 -ms 1.0 -sw 0.050 -ss 0.050 -o '+music)
    
    features=pd.read_csv('/home/scio/Desktop/2018_openHack/pyAudioAnalysis/test.wav_st.csv').as_matrix().mean(axis=0)
    featuresOfmusic.append([music,features[1],
                            features[2],
                            features[3],
                            features[4],
                            features[5],
                            features[6],
                            features[7],
                            features[8],
                            features[9],
                            features[22],
                            features[33]])