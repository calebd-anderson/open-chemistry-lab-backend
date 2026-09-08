package chemlab.infrastructure.pubchem;

public class PugApiConstants {
    public static final String PUG_PROLOG = "https://pubchem.ncbi.nlm.nih.gov/rest/pug";
    public static final String PUG_INPUT = "/compound/fastformula/";
    public static final String PUG_PROPERTIES_OPERATION = "/property/Title,InChIKey,MolecularWeight,ConnectivitySMILES,MolecularFormula,Fingerprint2D,Charge";
    public static final String PUG_CIDS_OPERATION = "/cids";
    public static final String PUG_OUTPUT = "/JSON";
}
