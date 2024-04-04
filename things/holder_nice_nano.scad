// microcontroller dimension

// nice!nano 
controllerWidth = 18;
controllerLen = 33.7;
controllerHeight = 1.7;


isExternalResetButtonEnabled = false;

buttonAdditionalHeight = 4.5;
controllerBoxHeight = 6;
controllerTopFaceHeight = 10;
controllerWallWidth = 1;
controllerBottomHeight = 1;
controllerHolderCylinderDiameter = 1.1;

//usb hole
usbHoleTopOffset = 0;
usbHoleHeight = 3.32;
usbConnectorWidth = 9;
usbOffset = 0.8;
//
controllerWiringHoleWidth = 4;
roundCornerHeight = 1;
roundCornerRadius = 1;
contactsWidth = 1.08;
contactsWallWidth = 2;

//bracing
bracingWidth = 1.5;
bracingLen = 1.9;

bracingOuterSize = 25;//bracingWidth*2*2 + controllerWidth + roundCornerRadius;//31.366; //32.5
echo(bracingOuterSize);

//button
buttonDiameter = 3;
buttonClickDiameter = 1.6;
buttonHeight = 5.2;
buttonWidth = 6;
buttonDepth = 1.5;
 
boxHeight = controllerBoxHeight; 
if (isExternalResetButtonEnabled) {
    boxHeight = boxHeight + buttonAdditionalHeight;
}

batteryLen = 67.5; 
batteryDiameter = 18.1;
batteryBoxHeight = batteryDiameter/2 + controllerBottomHeight;
isExternalResetButtonEnabled  = true;
isTmp = false;
//controllerLen = 8;

module usbHole() {
    width = usbConnectorWidth;
    radius = 1.2;
    height = usbHoleHeight;
    verticalDelta = height/2 - radius;
    holeDepth = usbOffset + controllerWallWidth;
    cubeWidth = width - radius * 2;
    
    translate([radius, 0, 0]) rotate([-90,0,00]) {
        translate([0, verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        translate([0, -verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        
        translate([cubeWidth, verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        translate([cubeWidth, -verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        
        // vert
        translate([0, -height/2 ,0]) cube([cubeWidth, height, holeDepth]);
        //hor
        translate([- radius, -height/2 + radius,0]) cube([width, height - radius*2, holeDepth]);
    }
}

module controllerHolderHelper() {
     offset = 10;
     translate([0,0,controllerHeight + + controllerBottomHeight + controllerHolderCylinderDiameter/2]){
                 translate([0, offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 translate([0, controllerLen - offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 
                 
                  translate([controllerWidth, offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 translate([controllerWidth, controllerLen - offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
        }    
}

module controllerBox() {
    // microcontroller holder
    union() {
        bracingTotalWidth = bracingWidth * 3;
        bracingLeft();
        bracingRight();
        
        
        controllerHolderHelper();
        
        if (isExternalResetButtonEnabled) {
            //button holder
            buttonBottomOffset = 0.5;
            holderWidth = buttonWidth + 1;
            depth = buttonDepth + 2;
            leftOffset = controllerWidth / 2 + roundCornerRadius;
            difference(){
            translate([leftOffset - holderWidth /2, controllerWallWidth , controllerBoxHeight - controllerWallWidth - 0.6]){
                       cube([holderWidth, depth, buttonAdditionalHeight + controllerWallWidth]);
                    }
                 switchHole();    
            }
        }
        
        difference() {
            union(){
                translate([0, roundCornerRadius, 0]){
                    minkowski() {
                        cube([controllerWidth,  controllerLen, controllerBoxHeight - roundCornerHeight]);
                        cylinder(roundCornerHeight, roundCornerRadius, roundCornerRadius, $fn=50);
                    }
                }
              
               //front wall
               translate([- (bracingOuterSize - controllerWidth - roundCornerRadius*2 - bracingWidth*3) , 0, 0]){
                    
                    cube([bracingOuterSize - 2 * bracingWidth*2, controllerWallWidth , controllerTopFaceHeight]);
                    
                }
                
            }
            // Microcontroller hole
            translate([0, roundCornerRadius, controllerBottomHeight]) cube([controllerWidth,  controllerLen, boxHeight]);
            
            holesFaceOffset = 2;
            // Right wiring hole
            translate([0, holesFaceOffset, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen -holesFaceOffset, controllerBottomHeight + 0.5]);
            
            // Left wiring hole
            translate([controllerWidth - controllerWiringHoleWidth, holesFaceOffset, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen - holesFaceOffset, controllerBottomHeight + 0.5]);
            
            usbConnectorLeftOffset = controllerWidth / 2 - usbConnectorWidth / 2;
            translate([usbConnectorLeftOffset, 0, usbHoleTopOffset + controllerBottomHeight + controllerHeight]) usbHole();
            
            
            if (isExternalResetButtonEnabled) {
                switchHole();
            }
        }
    }
}

module switchHole() {
    buttonBottomOffset = 0.5;
    //switcher button hole
    leftOffset = controllerWidth / 2 + roundCornerRadius;
    translate([leftOffset - (buttonWidth - buttonDiameter)/2, 0,  buttonDiameter/2 + controllerBoxHeight + buttonBottomOffset])
                rotate([-90, 0, 0]) {
                    cylinder(controllerWallWidth/2, buttonClickDiameter/2, buttonClickDiameter/2, $fn = 50);
                    translate([0, 0, controllerWallWidth/2]) cylinder(controllerWallWidth/2, buttonDiameter/2, buttonDiameter/2, $fn = 50);
    }
    //switcher case hole
    translate([leftOffset - buttonWidth/2 - controllerWallWidth, controllerWallWidth, controllerBoxHeight - controllerWallWidth + buttonBottomOffset]) {
        cube([buttonWidth, buttonDepth, buttonHeight]);
    }   
}

module bracingLeft() {
    width = bracingWidth*3;
    translate([- (bracingOuterSize - controllerWidth - roundCornerRadius*2 -bracingWidth) , 0, 0]){
    //translate([- bracingWidth *2, 0 , 0]) {
        difference() {
            cube([bracingWidth *2, bracingWidth*3, controllerTopFaceHeight]);
             translate([0, bracingWidth, 0])
            cube([bracingWidth, bracingLen, controllerTopFaceHeight]);
        }
        
    
    }
}

module bracingRight() {
   
    translate([controllerWidth, 0, 0]) {
        difference() {
            cube([bracingWidth *2, bracingWidth*3, controllerTopFaceHeight]);
            translate([bracingWidth, bracingWidth, 0])
            cube([bracingWidth, bracingLen, controllerTopFaceHeight]);
        }
    }
}

module batteryContactsHolder() {
    width = 4;
    height = batteryBoxHeight;
    depth = contactsWallWdth + contactsWidth;
    union() {
        difference(){
            cube([width, depth, height]);
            translate([0, 0, 0]) {
                cube([width , contactsWidth, height]);
                }
        }
        
        translate([batteryDiameter - width, 0 ,0 ]){ 
            difference(){
                cube([width, depth, height]);
                translate([0, 0, 0]) {
                    cube([width , contactsWidth, height]);
                }
            }
        }
    }
}

module batteryBox() {
    translate([0, controllerLen + roundCornerRadius, 0]) {     
        union() {
            // top
            translate([batteryDiameter/2, roundCornerRadius + (contactsWallWidth + contactsWidth), batteryDiameter/2]){
                difference() {
                    batteryCoverDiameter = batteryDiameter + controllerWallWidth+2;
                    rotate([-90,0,0]) cylinder(d=batteryCoverDiameter, h=batteryLen, $fn=100);
                    
                    
                    rotate([-90,0,0]) cylinder(d=batteryDiameter + 0.2, h=batteryLen + 1, $fn=100);
                    
                    translate([-batteryCoverDiameter/2, 0, -batteryCoverDiameter/2]){
                        cube([batteryCoverDiameter, batteryLen, batteryCoverDiameter/2]);
                    }
                    
                    topHoleLen = batteryLen - 20;
                    translate([-batteryCoverDiameter/2, (batteryLen - topHoleLen)/2, 0]){
                        cube([batteryCoverDiameter, topHoleLen, batteryCoverDiameter/2]);
                    }
                    
                }
            }
            
            //contacts 1
            translate([0, contactsWidth, 0]){
                batteryContactsHolder();
            }

            // contacts 2
            translate([0, batteryLen + 2 * (contactsWallWidth + contactsWidth) + roundCornerRadius, 0]){
                mirror([0,1,0]) batteryContactsHolder();
            }
            
            //battery
            if (isTmp){
                translate([batteryDiameter/2, roundCornerRadius + (contactsWallWidth + contactsWidth), batteryDiameter/2 + controllerBottomHeight]){
                    color("red") {
                        rotate([-90,0,0]) cylinder(d=batteryDiameter, h=batteryLen);
                    }
                }
            }

            difference(){  
                union(){
                    translate([0, roundCornerRadius, 0]){
                        minkowski() {
                            cube([batteryDiameter,  batteryLen + 2 * (contactsWallWidth + contactsWidth), batteryBoxHeight - roundCornerHeight]);
                                cylinder(roundCornerHeight, roundCornerRadius, roundCornerRadius, $fn=50);
                            }
                        }
                }
                
                // battery hole
               
                translate([0, roundCornerRadius, controllerBottomHeight]) {
                    cube([batteryDiameter,  batteryLen + 2 * (contactsWallWidth + contactsWidth), batteryBoxHeight]);
                }
                
                holesFaceOffset = contactsWallWidth + contactsWidth;
                 // wide hole
                translate([0, roundCornerRadius + holesFaceOffset, -0.5 ]) cube([batteryDiameter, batteryLen , controllerBottomHeight + 0.5]);
                
                centerHoleWidth = 8;
                // center hole
                translate([batteryDiameter/2 - centerHoleWidth/2, roundCornerRadius , -0.5 ]) cube([centerHoleWidth, batteryLen + 2*holesFaceOffset, controllerBottomHeight + 0.5]);
                
            }
        }
    }
}

controllerBox();
batteryBoxXOffset = -3;
translate([batteryBoxXOffset,0,0]) {
batteryBox();
}


