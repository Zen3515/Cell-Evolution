package sharedobject;

public interface Animatable {

	public void setAnimationState(AnimationState state);
	
	public enum AnimationState{IDELING, FORWARDING, FAST_FORWARDING, BACKWARDING, ATACKING_SPIKE, ATACKING_BITE}
}
