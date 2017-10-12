package communication;

public class ClassroomInitializationPackageRequest extends Update{
	
	private static final long serialVersionUID = -6839737537944657816L;
	
	private int classID;
	public ClassroomInitializationPackageRequest(int classID) {
		this.classID = classID;
	}
	
	public int getClassID() {
		return classID;
	}
}
