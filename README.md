# COE817-VirtualElectionBooth
COE817 Final Project

To run application, first run CTF.java followed by CLA.java, then you may run as many instances of Voter.java as youd like

If you wish to end the election and tally the votes, run a new instance of Voter.java and enter "exit" as the username.
Finally, Voter.java instances close after completion, however you must manually close CLA.java and CTF.java instances.

### Encryption

The CLA, CTF, and Voter generate a keypair, public and private keys at creation.

When the CLA connects to either the CTF or a voter, they share public keys. Then, the CLA sends a DES session key encrypted with the public key it recieves and uses this session key to encrypt and decrypt all communication the two sides.

In the case of the voter connecting to the CTF, the voter and CTF share public keys and the voter is the one that generates the DES session key and encrypts it using the CTF's public key. The session key is again used for all transmittions between the two.

This key distribution method is open to man in the middle attacks. A better key distribution protocol is needed, however better options depend on both parties knowing each other's public kys in advance which we don't have. In practice, the CTF and CLA will have to provide trusted certificates or use a KDC to exchange public keys.
