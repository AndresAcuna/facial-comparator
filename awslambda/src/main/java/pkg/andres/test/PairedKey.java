package pkg.andres.test;

import org.json.JSONObject;

final class PairedKey implements Comparable<PairedKey> {
    
    private final String partA;
    private final String partB;

    public PairedKey(String a, String b){
        partA = a;
        partB = b;
    }

    public boolean contains(String keyPart){
        return keyPart.equals(partA) || keyPart.equals(partB);
    }

    /**
     * Helper function to determine if the paired key shares
     * the same key parts with another shared key. This is 
     * useful because the value of Key<X,Y> = Key<Y,X>;
     * @param pairedKey key to be compared to
     * @return 0 if both have the same keys, -1 if not
     */
    public int compareTo(PairedKey pairedKey){
        return (pairedKey.contains(this.partA) && pairedKey.contains(this.partB)) ? 0 : -1;
    }

    // @Override
    // public boolean equals(Object obj){
    //     if(obj != null && obj instanceof PairedKey){
    //         PairedKey keyB = (PairedKey)obj;
    //         return contains(keyB.partA) && contains(keyB.partB);
    //     }
    //     return false;
    // }

    public String getPartA() {
		return this.partA;
	}

	public String getPartB() {
		return this.partB;
    }
    

    /**
     * Going to override the hashCode so that Pairedkey(A,B) will be stored
     * in the same bucket as Pairedkey(B,A)
     * 
     * This is only for this demo, because it's too simple an algorithm
     */
    @Override
    public int hashCode(){
        return partA.hashCode() * partB.hashCode();
    }

    public JSONObject getJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyA", this.partA);
        jsonObject.put("keyB", this.partB);

        return jsonObject;
    }

}