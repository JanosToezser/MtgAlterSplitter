MtgAlterSplitter 

Small program to separate the hand painted parts of altered Magic, the Gathering cards from the printed original.

This project started out some time ago when I got in contact with one of the owners of altersleeves.com while drawing
 some art for their newly founded business. They were planning to print art on sleeves for Magic cards, so that the 
 cards would appear altered but the card would not be damaged and keep its value. The task was to create some software
  with which artists could easily extract the parts that where drawn on any cards by them, from the original artwork 
  of those cards, so that the extracted image could be used by altersleeves and be printed on a sleeve.
  
 So far I have written some routines to 1.) at least partially, determine the altered parts of a card, 2.) remove artifacts 
 and 3.) fill some gaps.
 
 For 1.) I am using the observation, that the pixels in hand painted areas are more similar to their neighboring pixels 
 as they are in the printed areas of the card. This is due to the method that is used to print the cards, where CYMK 
 colors are used in different layers. Depending on the dpi of the scanned card, pixels form more or less uniform clusters 
 on the original card. At 600 dpi I was able to detect a pattern of clusters that was 2 by 2 pixels. HOWEVER, for some 
 colors, these clusters are asymmetrical. Another common cluster size is 1 by 2 but i think, especially for blues the 
 cluster could even be 1 by 5. When using symmetrical clusters I implemented a pixel shift for the clusters that are 
 being analyzed. This should compensate for shifted patterns due to minor variations that might have been introduced 
 during the scanning of the card.
 
 For 2.) I am assuming, that human artists are unable to create alterations that contain only a few pixels. Anything that 
 gets detected in step 1.) and is of a predefined size I deem "non-human" and the pixels will be erased from the final 
 picture. I do this in several loops. In these loops I increase the size for artifacts I want removed from the resulting 
 image.
 
 For 3.) I am looking at the image which steps 1.) and 2.) yielded and try to find pixels which where set to perfect black 
 in the previous steps, meaning, that either the detection of 1.) failed or the cleaning of step 2.) was a success. If enough 
 of the surrounding pixels are not perfect black I fill the position of the pixel with the pixel that is at the same position 
 in the original picture. I also do this in multiple passes.
 
 Right now, adjustments to the various variables can only be made in the code as I have yet to implement a GUI with which the 
 user will be able to set those values and receive a preview before saving the extracted image.