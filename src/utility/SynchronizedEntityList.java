package utility;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import exception.UnableToRandomExclusivelyException;
import javafx.geometry.Point2D;
import logic.entity.Entity;
import logic.entity.LivingEntity;
import logic.entity.MicrobePlayer;
import sharedobject.GameScreenCanvas;

//import logic.entity.Entity;

public class SynchronizedEntityList {
	
	private final ArrayList<Entity> entitisList;
	private MicrobePlayer player;
	private double bound;
	
//	Comparator<Entity> comparator = new Comparator<Entity>() {
//	    @Override
//	    public int compare(Entity left, Entity right) {
//	        if(left instanceof FoodEntity) {
//	        	return -1;
//	        } else {
//	        	return 0;
//	        }
//	    }
//	};
	
	public void clear() {
		entitisList.clear();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Entity> clone() {
		return (ArrayList<Entity>) entitisList.clone();
	}
//	
//	public void setPlayer(MicrobePlayer player) {
//		this.player = player;
//	}
//	
//	public SynchronizedEntityList() {
//		this(null);
//	}

	public SynchronizedEntityList(MicrobePlayer player) {
		// TODO Auto-generated constructor stub
		entitisList = new ArrayList<>();
		this.player = player;
		bound = Math.max(GameScreenCanvas.getWidth(), GameScreenCanvas.getHeight());
	}
	
	private ArrayList<LivingEntity> getLivingEntity() {
		ArrayList<LivingEntity> result = new ArrayList<>();
		result.add(player);
		for(Entity en : entitisList) {
			if(en instanceof LivingEntity) {
				result.add((LivingEntity) en);
			}
		}
		return result;
	}
	
	public LivingEntity randomEntityExclusively(Entity exclude) throws UnableToRandomExclusivelyException {
		ArrayList<LivingEntity> livingList = getLivingEntity();
		if(livingList.size() <= 1)
			throw new UnableToRandomExclusivelyException("This list contain only " + livingList.size() + " LivingEntity.");
		LivingEntity en = livingList.get(ThreadLocalRandom.current().nextInt(entitisList.size()));
		while(en == exclude) {
			en = livingList.get(ThreadLocalRandom.current().nextInt(entitisList.size()));
		}
		return en;
	}
	
	public MicrobePlayer getPlayer() {
		return this.player;
	}

	public synchronized void add(Entity en) {
		synchronized(entitisList) {
			entitisList.add(en);
//			Collections.sort(entitisList, comparator);
		}
//		System.out.println("Sorted = " + entitisList);
	}
	
	public void shiftBackground(Point2D velocity) {
		bound = Math.max(GameScreenCanvas.getWidth(), GameScreenCanvas.getHeight());
		player.move(velocity);
		for(Entity en : entitisList) {
			en.move(velocity);
		}
	}
	
	public Entity get(int index) {
		return entitisList.get(index);
	}
	
	public int size() {
		return entitisList.size();
	}
	
	public synchronized void checkRemoval() {
//		synchronized(entitisList) {
//			entitisList.removeIf(e -> isMarkForRemove(e));
//		}
		entitisList.removeIf(e -> isMarkForRemove(e));
	}
	
	private boolean isMarkForRemove(Entity en) {
		//check if pos is greather than current game width and height by two times
		if(en instanceof LivingEntity) {
			if(((LivingEntity) en).isAlive() == false)
				return true;
		}
		double distance = en.getPosition().distance(GameScreenCanvas.getCenter());
		if(distance > 1.5d*bound) {
			//TO make AI know that this is no longer active 
			if(en instanceof LivingEntity)
				((LivingEntity) en).setHealth(0);
			return true;
		} else {
			return false;
		}
	}
	
}
