# Clustering bank customers into 2 clusters based on their demographic 
# and account information. Then estimating the propensity of each cluster to 
# respond to an offer based on information of their response to past offers. 
# Attribute PEP is a binary variable indicating whether a customer has or has 
# not responded to past offers.

# The clustering algorithm used in K Means.

data<-read.csv("bankdata.csv")
data1<-data[,2:11]

#Normalizing Attribute Values
data1$income <- (data1$income - min(data1$income))/(max(data1$income)-min(data1$income))
data1$age <- (data1$age - min(data1$age))/(max(data1$age)-min(data1$age))

#initial centroid 
pivot<-sample(1:600,2,replace=F) #randomly picking two rows without replacement
#print(pivot)
cent<-data1[pivot,]
centNew <- cent


for( j in 1:20)
{	

  A <- data.frame()
  B <- data.frame()
  
  for(i in 1:600) { # Distance from Centroids
    distance1 = sqrt(sum((cent[1,]-data1[i,])^2))
    distance2 = sqrt(sum((cent[2,]-data1[i,])^2))
    if ( distance1 < distance2 ) {
      A <- rbind(A,data1[i,])
    } else {
      B <- rbind(B,data1[i,])
    }
  }
  
  centNew[1,] <- sapply(A, mean)
  centNew[2,] <- sapply(B, mean) 

if(sqrt(sum((cent[1,]-centNew[1,])^2))<0.001 && sqrt(sum((cent[2,]-centNew[2,])^2))<0.001)
{break}
else
{cent <- centNew} 

}

TrueForYes <- (data$pep==1)
rowNumbersA <- as.numeric(rownames(A))
rowNumbersB <- as.numeric(rownames(B))

#Sum of pep values
pepInA <- 0
pepInB <- 0

for( i in 1:length(TrueForYes) ) {
  if(TrueForYes[i] == TRUE) {
    if(i %in% rowNumbersA) {
      pepInA <- pepInA + 1
    } else {
      pepInB <- pepInB + 1
    }
  }
}


print("the size of each cluster is :")
print(length(A$age))
print(length(B$age))

print("the average values for the attribute PEP are :")
print(pepInA/length(A$age)) # length(B$age) used to calculate the no. of elements
print(pepInB/length(B$age))