Paper 1 Implementation.

Steps

1. Unzip `flows.zip` and get the folders `/ssh_flows_train` and `/ssh_test`
2. Run `python pcapParse.py ../ssh_flows_train ../pcap2txt`
3. Run `python pcapParse.py ../ssh_test ../new_pcap2txt`
4. Compile using `javac *.java`
5. Run `java -cp . Training ../pcap2txt`
6. Run `clustering.py`
7. Run `java -cp . Testing ../new_pcap2txt`

Dataset

Dataset of 10,380 pcap files has been split into 50:50 ratio for training and testing into the folders `/ssh_flows_train` and `/ssh_test`

Dependencies for `pcapParse.py`
	`pip install dpkt`
	`pip install scapy`
	`pip install pyshark`

Training Model

1. Log files are combined all together in the file `all_logs`, using the Python script `logsParse.py`
2. The pcap files from `/ssh_flows_train` are converted to text and stored in `/pcap2txt` using `pcapParse.py`
3. Compile using `javac *.java`
4. Run with `java -cp . Training ../pcap2txt`
5. The final subflow dataset is stored in `subflow_data.csv`

Clustering Model

1. To carry out clustering, run `python clustering.py`
2. Dependencies:
	`pip install scipy`
	`pip install numpy`
3. The dataset in `subflow_data.csv` is used to cluster, and the results of the model are stored in `model.txt`

Testing Model

1. Log files are combined all together in the file `all_test_logs`, using the Python script `logsParse.py`
2. The pcap files from `ssh_test` are converted to text and stored in `/new_pcap2txt` using `pcapParse.py`
3. Compile using `javac *.java`
4. Run with `java -cp . Testing ../new_pcap2txt`
5. The accuracy of the model is printed at the end of testing

Classes

1. Training: contains a main function, and all methods to conduct the training tasks
2. Testing: contains a main function, and all methods to conduct the testing tasks
3. Flow
4. Packet
5. PacketPair
6. SubFlow

3-6 are self explanatory

Checking points
- Transition point 2: encrypted packet logic
- Transition point is packet or packet pair?
- Zalpha and Zbeta
- Transition point identification accuracy

