package game.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import logic.cellpart.CellPart;
import logic.cellpart.CellPart.CellPartType;
import logic.entity.MicrobeEntity;
import logic.entity.MicrobePlayer;
import sharedobject.CellEvolutionManager;
import sharedobject.GameManager;
import utility.BezierCurved;
import utility.RGBColor;

public class EvolveCanvas extends Canvas {
	
	private static final double LEFT_CONTROL_WIDTH = 250;
	private static final int BONE_WIDTH = 10;
	private static final int BONE_LENGHT = 60;
	private static final int BONE_GAP = 10;
	private static final double DEFAULT_ERROR_MESSAGE_OPACITY = 5;
	private static final double DEFAULT_ERROR_MESSAGE_DECREASE_RATE = 0.065;
	
	private GraphicsContext gc;
	private static final Paint BACKGROUND = new LinearGradient(1, 1, 0.5, 0.75, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.WHITESMOKE), new Stop(1, Color.WHITE)});
	private CellPartType selectedPartType;
	private MicrobeEntity microbe;
	private double mouseX = 0;
	private double mouseY = 0;
	private int dragCounter = 0;
	private boolean isSelectedBody = false;
	private boolean isClicked = false;
	private boolean isLeftDown = false;
	private boolean isLeftClickedLastTick = false;
	
	private boolean isInBuyingState = false;
	private double buyingPosX = 0;
	private double buyingPosY = 0;
	private double buyingAngle = 0;
	
	private double errorMessageOpacity = 0.0d;
	private String errorMessage = "";
	
	private RGBColor color1 = new RGBColor();
	private RGBColor color2 = new RGBColor();

	private BoundingBox partViewer;
	private BoundingBox flagellumBtn;
	private BoundingBox eyeBtn;
	private BoundingBox carnivoreMouthBtn;
	private BoundingBox herbivoreMouthBtn;
	private BoundingBox spikeBtn;
	
	private BoundingBox resetCellPartBtn;
	private BoundingBox acceptedBtn;
	
	private BoundingBox boneBox;
	private int selectedBoneIndex = -1;

	private BoundingBox lenghtAdd;
	private BoundingBox lenghtSub;
	private BoundingBox sizeAdd;
	private BoundingBox sizeSub;
	private BoundingBox color1Add;
	private BoundingBox color1Sub;
	private BoundingBox color2Add;
	private BoundingBox color2Sub;
	
	private Timeline animationTimeline;
	
	public EvolveCanvas(MicrobePlayer player) {
		// TODO Auto-generated constructor stub
		super(CellEvolutionManager.DEFAULT_GAME_SCREEN_WIDTH, CellEvolutionManager.DEFAULT_GAME_SCREEN_HEIGHT);
		gc = getGraphicsContext2D();
		microbe = new MicrobeEntity(new Point2D(LEFT_CONTROL_WIDTH + ((getWidth() - LEFT_CONTROL_WIDTH)/2.0d), 300), 150, 50);
//		MicrobeEntity.initializeAllCellPartInstance(microbe);
		
		microbe.setBone(player.getBone());
		
		initializeButton();
		addListener();
		animationTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0d / 60.0d), event -> {
			update();
		}));
		animationTimeline.setCycleCount(Timeline.INDEFINITE);
		
		paintComponient();
	}
	
	private void addListener() {
		this.setOnMouseMoved(event -> {
			mouseX = event.getX();
			mouseY = event.getY();
		});
		this.setOnMousePressed(event -> {
			if(event.getButton() == MouseButton.PRIMARY) {
//				System.out.println("Pri down");
				isLeftDown = true;
				isLeftClickedLastTick = true;
			}
		});
		this.setOnMouseReleased(event -> {
			if(event.getButton() == MouseButton.PRIMARY) {
//				System.out.println("Pri up");
				if(dragCounter < 5) {
					isClicked = true;
				}
				mouseX = event.getX();
				mouseY = event.getY();
				isLeftDown = false;
			}
		});
		this.setOnMouseDragged(event -> {
			mouseX = event.getX();
			mouseY = event.getY();
		});
		this.setOnScroll(event -> {
			if(isInBuyingState) {
				if(event.getDeltaY() > 0) {
//					System.out.println("Increasing angle");
					buyingAngle = (buyingAngle + 1) % 360;
				} else {
					buyingAngle = (buyingAngle - 1);
					if(buyingAngle < 0) {
						buyingAngle += 360;
					}
				}
			}
		});
	}
	
	private void initializeButton() {
//		double xPos = LEFT_CONTROL_WIDTH/2.0d;
		partViewer = new BoundingBox(25, 25, 200, 200);
		
		flagellumBtn = new BoundingBox(0, 245, LEFT_CONTROL_WIDTH, 35);
		eyeBtn = new BoundingBox(0, 285, LEFT_CONTROL_WIDTH, 35);
		carnivoreMouthBtn = new BoundingBox(0, 325, LEFT_CONTROL_WIDTH, 35);
		herbivoreMouthBtn = new BoundingBox(0, 365, LEFT_CONTROL_WIDTH, 35);
		spikeBtn = new BoundingBox(0, 405, LEFT_CONTROL_WIDTH, 35);
		
		double xSub = LEFT_CONTROL_WIDTH - 60;
		lenghtAdd = new BoundingBox(25, 465, 35, 35);
		lenghtSub = new BoundingBox(xSub, 465, 35, 35);
		sizeAdd = new BoundingBox(25, 505, 35, 35);
		sizeSub = new BoundingBox(xSub, 505, 35, 35);
		color1Add = new BoundingBox(25, 545, 35, 35);
		color1Sub = new BoundingBox(xSub, 545, 35, 35);
		color2Add = new BoundingBox(25, 585, 35, 35);
		color2Sub = new BoundingBox(xSub, 585, 35, 35);
		
		updateBoneButton();
		
		resetCellPartBtn = new BoundingBox(50, getHeight() - 75, 150, 35);
		acceptedBtn = new BoundingBox(50, getHeight() - 150, 150, 35);
		//TODO add the rest button
	}
	
	private void updateBoneButton() {
		double xPos = microbe.getPosition().getX() - (BONE_WIDTH/2.0d);
		double yPos = microbe.getPosition().getY() - (microbe.getBodyLenght()/2.0d);
		int boneCount = microbe.getBone().size();
		boneBox = new BoundingBox(xPos, yPos, BONE_WIDTH, ((BONE_LENGHT*boneCount) + (BONE_GAP*(boneCount-1))));
	}
	
	public void switchTo(MicrobePlayer player) {
//		this.getParent().requestFocus();
//		this.requestFocus();
//		paintComponient();
		animationTimeline.play();
//		MicrobeEntity.parseCellPartInstance(player, microbe);
		MicrobeEntity.parseEntity(player, microbe);
		updateMicrobeBody();
		updateBoneButton();
	}
	
	public void goToMenu() {
		animationTimeline.stop();
		updateMicrobeBody();
		MicrobeEntity.calculateAttribute(microbe);
		MicrobeEntity.parseEntity(microbe, CellEvolutionManager.getInstance().getPlayer());
		this.setFocused(false);
		GameManager.switchToMenu();
	}
	
	private void showErrorMessage(String message) {
		this.errorMessage = message;
		this.errorMessageOpacity = DEFAULT_ERROR_MESSAGE_OPACITY;
	}
	
	private void checkLeftButtonClick() {
		Point2D clickedPos = new Point2D(mouseX, mouseY);
		if(flagellumBtn.contains(clickedPos)) {
			this.selectedPartType = CellPartType.FLAGELLUM;
		} else if(eyeBtn.contains(clickedPos)) {
			this.selectedPartType = CellPartType.EYE;
		} else if(carnivoreMouthBtn.contains(clickedPos)) {
			this.selectedPartType = CellPartType.CARNIVORE_MOUTH;
		} else if(herbivoreMouthBtn.contains(clickedPos)) {
			this.selectedPartType = CellPartType.HERBIVORE_MOUTH;
		} else if(spikeBtn.contains(clickedPos)) {
			this.selectedPartType = CellPartType.SPIKE;
		} else if(lenghtAdd.contains(clickedPos)) {
//			System.out.println("lenght add");
			editBoneLenght(true);
		} else if(lenghtSub.contains(clickedPos)) {
//			System.out.println("lenght sub");
			editBoneLenght(false);
		} else if(sizeAdd.contains(clickedPos)) {
//			System.out.println("size add");
			if(selectedPartType != null) {
				showErrorMessage("Deselect the part first");
				return;
			}
			editBoneSize(true);
		} else if(sizeSub.contains(clickedPos)) {
//			System.out.println("size sub");
			if(selectedPartType != null) {
				showErrorMessage("Deselect the part first");
				return;
			}
			editBoneSize(false);
		} else if(color1Add.contains(clickedPos)) {
//			System.out.println("color1 add");
			color1.increase();
			changeColor(selectedPartType == null);
		} else if(color1Sub.contains(clickedPos)) {
//			System.out.println("color1 sub");
			color1.decrease();
			changeColor(selectedPartType == null);
		} else if(color2Add.contains(clickedPos)) {
			color2.increase();
//			System.out.println("color2 add");
			changeColor(selectedPartType == null);
		} else if(color2Sub.contains(clickedPos)) {
//			System.out.println("color2 sub");
			color2.decrease();
			changeColor(selectedPartType == null);
		} else if(resetCellPartBtn.contains(clickedPos)) {
//			System.out.println("reset");
			if(selectedPartType == null) {
				showErrorMessage("SELECT THE PART FIRST!");
				return;
			}
			MicrobeEntity.resetCellPart(microbe, selectedPartType);
		}  else if(acceptedBtn.contains(clickedPos)) {
//			System.out.println("EVOLVE!!");
			goToMenu();
//			MicrobeEntity.resetCellPart(microbe, selectedPartType);
		}else {
			selectedBoneIndex = -1;
		}
	}
	
	private void editBoneLenght(boolean isIncrease) {
		if(isIncrease) {
			if(microbe.getBone().size() >= 6) {
				showErrorMessage("Your species has become too complex");
				return;
			}
			microbe.getBone().add(35.0d);
		} else {
			if(microbe.getBone().size() <= 3) {
				showErrorMessage("Your species is too small");
				return;
			}
			microbe.getBone().remove(microbe.getBone().size()-1);
		}
		updateMicrobeBody();
		MicrobeEntity.updateCellPartPosition(microbe);
		updateBoneButton();
	}
	
	private void editBoneSize(boolean isIncrease) {
		if(selectedBoneIndex == -1) {
			showErrorMessage("SELECT BODY FIRST");
			return;
		}
		if(isIncrease) {
			MicrobeEntity.editBone(microbe, selectedBoneIndex, 10);
		} else {
			MicrobeEntity.editBone(microbe, selectedBoneIndex, -10);
		}
		updateMicrobeBody();
		MicrobeEntity.updateCellPartPosition(microbe);
	}
	
	private void changeColor(boolean isBase) {
		if(isBase) {
			MicrobeEntity.changeBodyColor(microbe, color1.getColor(), color2.getColor());
			return;
		}
		MicrobeEntity.changeColor(microbe, selectedPartType, color1.getColor(), color2.getColor());
	}
	
	private void checkBoneButton() {
		if(!boneBox.contains(new Point2D(mouseX, mouseY))) {
			return;
		}
//		System.out.println("boneBox");
		double y = mouseY - (microbe.getPosition().getY() - (microbe.getBodyLenght()/2.0d));
		double stepper = 60;
		int index = 0;
		while(y > stepper) {
			index += 1;
			stepper += 70;
		}
		selectedBoneIndex = index;
//		System.out.println("Bone Inex = " + index);
	}
	
	private void update() {
//		System.out.println("Mouse at " + mouseX + ", " + mouseY);
		if(isLeftDown && !isLeftClickedLastTick) {
			dragCounter++;
		} else {
			dragCounter = 0;
		}
		
		if(errorMessageOpacity > 0) {
			errorMessageOpacity -= DEFAULT_ERROR_MESSAGE_DECREASE_RATE;
			if(errorMessageOpacity < 0) {
				errorMessageOpacity = 0;
			}
		}
		
		if(isClicked) {
//			System.out.println("Click");
			if(!isInBuyingState) {
				if(mouseX < LEFT_CONTROL_WIDTH) {
//					System.out.println("Click under left");
					
					checkLeftButtonClick();
				} else {
					selectedPartType = null;
					//System.out.println("Click under right");
					if(microbe.getBound().contains(mouseX, mouseY)) {
						isSelectedBody = true;
					} else {
						isSelectedBody = false;
					}
					checkBoneButton();
					
				}
			} else {
				checkBuyingButton();
			}
		} else if(dragCounter > 10) {
			//System.out.println("Draging");
			if(!isInBuyingState && partViewer.contains(mouseX, mouseY)) {
				isInBuyingState = true;
			}
			updateDraging();
		}
		isClicked = false;
		isLeftClickedLastTick = false;
		paintComponient();
	}
	
	private void checkBuyingButton() {
		if(acceptedBtn.contains(mouseX, mouseY)) {
			double rawX = buyingPosX - microbe.getPosition().getX();
			double x = Math.abs(rawX);
			double y = buyingPosY - (microbe.getPosition().getY() - (microbe.getBodyLenght()/2.0d));
			if(MicrobeEntity.isCellPartInAppropriatePosition(microbe, x, y)) {
				if(rawX > 0 && selectedPartType == CellPartType.FLAGELLUM) {
					buyingAngle = -buyingAngle;
				} else if(rawX < 0 && selectedPartType != CellPartType.FLAGELLUM){
					buyingAngle = -buyingAngle;
					//buyingAngle += 180;
				}
				if(selectedPartType == CellPartType.SPIKE) {
					microbe.addCellPart(selectedPartType, x, y, buyingAngle, x > 5, 10, 110);
				} else {
					microbe.addCellPart(selectedPartType, x, y, buyingAngle, x > 5);
				}
				this.buyingAngle = 0;
				this.isInBuyingState = false;
			} else {
				showErrorMessage("Please move this part inside your cell first.");
			}
		} else if (resetCellPartBtn.contains(mouseX, mouseY)) {
			//Cancel
			this.buyingAngle = 0;
			this.isInBuyingState = false;
		}
	}
	
	private void updateMicrobeBody() {
		int size = (microbe.getBone().size()+2)*2;
		double[] bezierInput = new double[size];
		bezierInput[0] = 0;
		bezierInput[1] = 0;
		
		bezierInput[2] = 0;
		bezierInput[3] = microbe.getBone().get(0);
		
		int bi = 4;
		
		double xi = (BONE_LENGHT*1.5) + BONE_GAP;
		double yi = 0;
		for(int i = 1; i < microbe.getBone().size()-1; i++) {
			bezierInput[bi] = xi;
			yi =  microbe.getBone().get(i);
			bezierInput[bi+1] = yi;
			bi += 2;
			xi += BONE_LENGHT + BONE_GAP;
		}
		xi += (BONE_LENGHT*1.5) + BONE_GAP;
		bezierInput[size-4] = xi;
		bezierInput[size-3] = microbe.getBone().get(microbe.getBone().size()-1);
		bezierInput[size-2] = xi;
		bezierInput[size-1] = 0;
		
//		for(int i = 0; i < size; i += 2) {
//			System.out.println(bezierInput[i] + ", " + bezierInput[i+1]);
//		}
		
		size = microbe.getBone().size();
//		double[] retrive = BezierCurved.Bezier2D(bezierInput, (int)(((BONE_LENGHT*size) + (BONE_GAP*(size-1)))/2.0d));
//		double[] cellBody = new double[retrive.length/2];
//		
//		System.out.println("cellBody size = " + cellBody.length);
//		for(int i = 0; i < cellBody.length; i++) {
//			cellBody[i] = retrive[(2*i) + 1];
//		}
		double[] cellBody = BezierCurved.Bezier2D(bezierInput, (int)(((BONE_LENGHT*size) + (BONE_GAP*(size-1)))/2.0d));
		cellBody[0] = cellBody[1]/2.0d;
		cellBody[cellBody.length-1] = cellBody[0];
//		for(double bd : cellBody) {
//			System.out.println("bd = " + bd);
//		}
		microbe.setCellBody(cellBody);
	}

	private void updateDraging() {
		buyingPosX = mouseX;
		buyingPosY = mouseY;
	}
	
	public void paintComponient() {
		gc.setFont(new Font(20));
		drawBackground();
		drawMicrobe();
		drawLeftControl();
		drawBuyingComponient();
		drawDragingPart();
		drawErrorMessage();
	}
	
	private void drawDragingPart() {
		if(!isInBuyingState) {
			return;
		}
		CellPart.drawStill(gc, selectedPartType, microbe, buyingPosX, buyingPosY, buyingAngle);
	}

	private void drawBuyingComponient() {
		if(!isInBuyingState) {
			return;
		}
		gc.setFill(Color.grayRgb(255, 0.7));
		gc.fillRect(0, 0, LEFT_CONTROL_WIDTH, getHeight());
		
		double controlCenterX = LEFT_CONTROL_WIDTH/2.0d;
		drawRoundButton(controlCenterX-75, getHeight() - 75, 150, 35, "Cancel", Color.RED, false);
		drawRoundButton(controlCenterX-75, getHeight() - 150, 150, 35, "Accept", Color.LIMEGREEN, false);
	}
	
	private void drawErrorMessage() {
		if(this.errorMessageOpacity <= 0) {
			return;
		}
		double opacity = this.errorMessageOpacity;
		if(opacity > 1)
			opacity = 1;
		gc.setFill(Color.RED);
		gc.fillText(errorMessage, getWidth()/2, 50);
	}
	
	private void drawBackground() {
		gc.setFill(BACKGROUND);
		gc.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void drawPartViewer(double x, double y) {
		gc.setFill(Color.grayRgb(150, 0.7));
		gc.fillRect(x, y, 200, 200);
		if(selectedPartType != null) {
			x += 100;
			y += 100;
			if(selectedPartType == CellPartType.SPIKE) {
				y += 80;
			}
			CellPart.drawStill(gc, selectedPartType, microbe, x, y, 0);
		}
	}
	
	private void drawPartName(double x, double y, String text, Color color, boolean isHilight) {
		if(isHilight) {
			gc.setFill(Color.grayRgb(158, 0.6));
			gc.fillRect(0, y-5, LEFT_CONTROL_WIDTH, 35);
		}
		gc.setFill(color);
		gc.fillText(text, x, y);
	}
	
	private void drawLeftControl() {
		gc.setFill(Color.grayRgb(0, 0.7));
		gc.fillRect(0, 0, LEFT_CONTROL_WIDTH, getHeight());
		drawPartViewer(25,25);
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.TOP);
		
		double controlCenterX = LEFT_CONTROL_WIDTH/2.0d;
		
		drawPartName(controlCenterX, 250, "Flagellum", Color.WHITE, (selectedPartType != null && selectedPartType == CellPartType.FLAGELLUM));
		drawPartName(controlCenterX, 290, "Eye", Color.WHITE, (selectedPartType != null && selectedPartType == CellPartType.EYE));
		drawPartName(controlCenterX, 330, "Carnivore Mouth", Color.WHITE, (selectedPartType != null && selectedPartType == CellPartType.CARNIVORE_MOUTH));
		drawPartName(controlCenterX, 370, "Herbivore Mouth", Color.WHITE, (selectedPartType != null && selectedPartType == CellPartType.HERBIVORE_MOUTH));
		drawPartName(controlCenterX, 410, "Spike", Color.WHITE, (selectedPartType != null && selectedPartType == CellPartType.SPIKE));
	
//		gc.setTextAlign(TextAlignment.LEFT);
		
		gc.fillText("Lenght", controlCenterX, 470);
		gc.fillText("Size", controlCenterX, 510);
		gc.fillText("Color 1", controlCenterX, 550);
		gc.fillText("Color 2", controlCenterX, 590);
		
		drawSquareButton(25, 465, 35, 35, "+", Color.LIMEGREEN, false);
		drawSquareButton(LEFT_CONTROL_WIDTH - 60, 465, 35, 35, "-", Color.RED, false);
		drawSquareButton(25, 505, 35, 35, "+", Color.LIMEGREEN, false);
		drawSquareButton(LEFT_CONTROL_WIDTH - 60, 505, 35, 35, "-", Color.RED, false);
		drawSquareButton(25, 545, 35, 35, "+", Color.LIMEGREEN, false);
		drawSquareButton(LEFT_CONTROL_WIDTH - 60, 545, 35, 35, "-", Color.RED, false);
		drawSquareButton(25, 585, 35, 35, "+", Color.LIMEGREEN, false);
		drawSquareButton(LEFT_CONTROL_WIDTH - 60, 585, 35, 35, "-", Color.RED, false);
		

		drawRoundButton(controlCenterX-75, getHeight() - 150, 150, 35, "Evolve!", Color.LIMEGREEN, false);
		drawRoundButton(controlCenterX-75, getHeight() - 75, 150, 35, "ResetPart", Color.RED, false);
	}
	
	private void drawSquareButton(double x, double y, double width, double height, String text, Color color, boolean isClicked) {
		if(isClicked) {
			color = color.brighter();
		}
		gc.setFill(color);
		gc.fillRect(x, y, width, height);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFill(Color.BLACK);
		gc.fillText(text, x + (width/2.0d), y + (height/2.0d));
	}
	
	private void drawRoundButton(double x, double y, double width, double height, String text, Color color, boolean isClicked) {
		if(isClicked) {
			color = color.brighter();
		}
		gc.setFill(color);
		gc.fillRoundRect(x, y, width, height, 10, 10);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFill(Color.BLACK);
		gc.fillText(text, x + (width/2.0d), y + (height/2.0d));
	}
	
	private void drawMicrobeBone() {
		if(!isSelectedBody) {
			return;
		}
		gc.setFill(Color.grayRgb(255, 0.9));
		gc.fillRect(LEFT_CONTROL_WIDTH, 0, getWidth() - LEFT_CONTROL_WIDTH, getHeight());
		
		double xPos = microbe.getPosition().getX() - (BONE_WIDTH/2.0d);
		double yPos = microbe.getPosition().getY() - (microbe.getBodyLenght()/2.0d);
		
		//Beware that x is y and y is x
		
		gc.setFill(Color.ORANGE);
		for(int i = 0; i < microbe.getBone().size(); i++) {
			if(i == selectedBoneIndex) {
				gc.setFill(Color.ORANGERED);
				gc.fillRect(xPos, yPos, BONE_WIDTH, BONE_LENGHT);
				gc.setFill(Color.ORANGE);
				yPos += BONE_LENGHT + BONE_GAP;
				continue;
			}
			gc.fillRect(xPos, yPos, BONE_WIDTH, BONE_LENGHT);
			yPos += BONE_LENGHT + BONE_GAP;
		}
		
	}
	
	private void drawMicrobe() {
		MicrobeEntity.drawStill(microbe, gc);
		if(selectedPartType == null) {
			drawMicrobeBone();
		}
	}
}
