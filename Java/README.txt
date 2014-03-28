Context:
If you walk through the halls of companies like Facebook, Google, and Amazon, you will see large displays hung on the walls showing trends. For example, at Google you might see the 20 search terms that have been most common in the past 10 minutes; at Facebook, the 10 people which have been friended the most times in the past one hour. These displays update continuously.


Objective:  
Implement a LogProcessor interface that performs part of this functionality. Specifically, you will implement an add(url,time)  method and a getOrderedUrlsInWindow(K) method, which returns a list of the K most commonly viewed URLs in the W seconds previous to the URL with the most recent timestamp. The implementing class constructor creates a LogProcessor class with the specified W.

This code implements this functionality using the Collections package in the LogProcessorFast method and without it in the LogProcessorSlow method.

It makes allowance for cases where calls to add may have values for time that are not always in increasing order. (If add is always called with increasing values for time, then a simple queue suffices.) Another subtlety is that there may be more than one add call with the same page and time.

