# Disaster Alerting System
 Apache Spark streaming application using Twitter4j and developer account (provides OAuth keys) to stream live tweets.
 
 Using NLP techniques to process tweets and create embeddings from them (numerical vectors which the machine learning algorithm can understand).
 
 The algorithm used here is not perfect: using the Word2Vec model should be changed with GloVe embeddings, and the Random Forest classifier should be changed with Neural Network.
 
 There are some extensions to it, which are already prepared, like adding other features (characters count, numbers count, URLs count, and so on). These may boost the performance of the model.
 
 All the trained models have been saved in the resources. The final pipeline is created loading them one by one because Spark does not support writing pipelines with custom transformers. After loading models, the program must load the dataset too, because Spark cannot create PipelineModel out of transformers, instead, an Estimator is created, which must be fitted to get the PipelineModel.
 ## :gear: Requirements and building
 
 ```git
 git clone https://github.com/Goader/disaster-alerting-spark.git
 ```
 
 * Scala 2.12.10
 * Apache Spark 3.1.1
 * Apache Bahir (Spark Streaming Twitter) 2.4.0
 * Play JSON Parser 2.9.2
 * Twitter4j 4.0.6
 
 _To run the application - use SBT._
 
 There are 2 App objects:
   * `Application` - main application object, which runs the program
   * `ModelTrainingApp` - script used for model training, using Grid Search and Cross Validation
 
 ## :keyboard: Created by [@Goader](https://github.com/Goader)
