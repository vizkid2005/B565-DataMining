from multiprocessing import Pool
import os
import sys
import random

path = "/N/dc2/scratch/hydargah/dm/"
bigDict = {}
def main():
	path = "/N/dc2/scratch/hydargah/dm/"
	labels = list(i for i in range(1,10))
	
	p = Pool(len(labels))
	allCounts = p.map(countLabel, labels)
	print allCounts
	priors = calculatePriors(allCounts)
	classifySamples(priors)
	classifySamples(priors)
	
def countLabel(label):
	tempCounter = 0
	f = open(path+"trainLabels.csv")
	for line in f:
		split = line.split(",") 		
		currentLabel = split[1][:len(split[1])-1]
		if currentLabel == str(label):
			tempCounter = tempCounter +1
	f.close()
	print "Label "+str(label)+" has count "+str(tempCounter)
	return (label,tempCounter)

def calculatePriors(allCounts):
	total = 0
	for tuple in allCounts:
		total = total + tuple[1]
	labelProb = {}
	for tuple in allCounts:
		count = tuple[1]
		prob = float(count/total)
		labelProb[tuple[0]] = prob
	
	return labelProb

def classifySamples(priors):
	r = random.random()
	sum = 0
	result = 1
	for i in range(1,10):
		prob = priors[str(i)]
		sum += prob
		if r < sum:
			print i
			break
		result += 1
	
if __name__ == "__main__": main()
