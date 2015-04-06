from multiprocessing import Pool
import os
from csv import writer
import time

read_mode, write_mode = ('r','w')

path = "/N/dc2/scratch/hydargah/dm/trainRaw/"
#path = "/media/vizkid2005/Elements/MicrosoftData/train/"
#path = os.getcwd()+"/bytefiles/"

def clean(filename):
	print filename
	countFile = open("/N/dc2/scratch/hydargah/dm/data.train", 'a')
	f = writer(countFile)
	currentFile = open(path+filename, read_mode)
	twoByte = [0]*16**2
	no_que_mark = 0
	for row in currentFile:
		codes = row[:-2].split()[1:]
		no_que_mark += codes.count('??')
		twoByteCode = [int(i,16) for i in codes if i != '??']

		for i in twoByteCode:
			twoByte[i] += 1

	vector = []
	vector.append([filename,no_que_mark,]+ twoByte);
	f.writerows(vector)
	print "Written for file "+filename

	#del vector,countFile,f,twoByteCode,twoByte,currentFile,no_que_mark,codes

def main():
	countFile = open("/N/dc2/scratch/hydargah/dm/data.train",write_mode)
	colnames = ['filename', 'no_que_mark']
	colnames += ['TB_'+hex(i)[2:] for i in range(16**2)]
	f = writer(countFile)
	f.writerow(colnames)
	
	fileNames = [f for f in os.listdir(path) if ".asm" not in f]
	
	p = Pool(32)
	p.map(clean, fileNames)

def consolidate(path):
	fileNames = os.listdir(path)
	countFile = open(os.getcwd()+"/data.train",write_mode)
	colnames = ['filename', 'no_que_mark']
	colnames += ['TB_'+hex(i)[2:] for i in range(16**2)]
	f = writer(countFile)
	f.writerow(colnames)

	featureVectors = []
	for t, name in enumerate(fileNames):
		currentFile = open(path+name,read_mode)
		twoByte = [0]*16**2
		no_que_mark = 0
		for row in currentFile:
			codes = row[:-2].split()[1:]
			no_que_mark += codes.count('??')
			twoByteCode = [int(i,16) for i in codes if i != '??']

			for i in twoByteCode:
				twoByte[i] += 1

		featureVectors.append([name,no_que_mark,]+ twoByte);
		
		if t%100 ==0:
			print "Working ... Please wait ... "
			f.writerows(featureVectors)
			featureVectors= []

	if len(featureVectors) > 0:
		f.writerows(featureVectors)

	del featureVectors, fileNames, colnames		
	#for fname in fileNames:
	#	file = open(path+fname)


def test(i):
	print i

if __name__ == "__main__": 
	#print path
	start = time.time()
	main()
	print str(time.time() - start)
	#consolidate(path)
	#p = Pool(1)
	#p.map(consolidate, path)
