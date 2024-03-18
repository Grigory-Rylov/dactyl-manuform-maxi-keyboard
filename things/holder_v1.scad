// microcontroller dimension

// rp2040 black
controllerWidth = 23;
controllerLen = 54.3;
controllerHeight = 1.5;
controllerHolderHeight = 12.9;
// rp2040 purple
//controllerWidth = 21;
//controllerLen = 55;
//controllerHeight = 1.7;
//controllerHolderHeight = 10;

isExternalResetButtonEnabled = false;

buttonAdditionalHeight = 4.5;
controllerBoxHeight = 12.9;
controllerWallWidth = 1;
controllerBottomHeight = 2.2;
controllerHolderCylinderDiameter = 1.5;

//usb hole
usbHoleTopOffset = 1;
usbHoleHeight = 9.5;
usbHoleWidth = 13.5;
usbConnectorWidth = 9;

// trrs
trrsDiameter = 6.2; // real size 5.8
trrsLen1 = 4;
trrsLen2 = 12;
trrsContactsLen = 6.5;
trrsOutterDiameter = 8;

//
controllerWiringHoleWidth = 6;
roundCornerHeight = 1;
roundCornerRadius = 1;

//bracing
bracingWidth = 1.5;
bracingLen = 1.9;

bracingOuterSize = 31.366; //32.5

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

isTmp = false;
//controllerLen = 8;

module trrs() {
    color("green")
    translate([-trrsOutterDiameter/2 - controllerWallWidth, 0, trrsOutterDiameter/2 + controllerBottomHeight])
    rotate([-90,0,00]) {
        translate([0, 0, -trrsLen1]) {
            cylinder(trrsLen1, trrsDiameter/2, trrsDiameter/2, $fn=50);
            translate([0, 0, trrsLen1]) {
                cylinder(trrsLen2, trrsOutterDiameter/2, trrsOutterDiameter/2, $fn=50);
                contactsDiameter = trrsOutterDiameter - 2;
                translate([0,0, trrsLen2]) cylinder(trrsContactsLen, contactsDiameter/2, contactsDiameter/2, $fn=50);
            }
        }
    } 
}

module usbCableHole() {
    radius = 3;
    holeDepth = controllerWallWidth + 3 * bracingWidth;
    cubeWidth = usbHoleWidth - radius * 2;
    if (cubeWidth < 0) {
        cubeWidth = 0;
    }
    verticalDelta = usbHoleHeight/2 - radius;
    translate([radius, 0, 0]) rotate([-90,0,00]) {
//        cylinder(holeDepth, usbHoleHeight/2, usbHoleHeight/2, $fn=50);
        
        translate([0, verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        translate([0, -verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        
        translate([cubeWidth, verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        translate([cubeWidth, -verticalDelta, 0]) cylinder(holeDepth, radius, radius, $fn=50);
        
        // vert
        translate([0, -usbHoleHeight/2 ,0]) cube([cubeWidth, usbHoleHeight, holeDepth]);
        //hor
        translate([- radius, -usbHoleHeight/2 + radius,0]) cube([usbHoleWidth, usbHoleHeight - radius*2, holeDepth]);
    }    
}

module usbHole() {
    width = usbConnectorWidth;
    height = 4.7;
    radius = 1.2;
    verticalDelta = height/2 - radius;
    holeDepth = 1;
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
     offset = 17;
     translate([0,0,controllerHeight + controllerBottomHeight + controllerHolderCylinderDiameter/2]){
                 translate([0, offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 translate([0, controllerLen - offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 
                 
                  translate([controllerWidth, offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
                 translate([controllerWidth, controllerLen - offset, 0]) rotate([-90,0,00]) { cylinder(h = 3, d = controllerHolderCylinderDiameter, center = true, $fn=20);}
        }    
}

module controllerBox() {
    
    union() {
        // microcontroller holder
        if(!isTmp) {
            controllerHolderHelper();
        }
        
        difference() {
            union(){
                bracingLeft();
                //bracing left
                translate([-trrsOutterDiameter  - bracingWidth *2, -controllerWallWidth, 0]) {
               
                    //rigth
                    translate([bracingOuterSize - bracingWidth *2, 0, 0])
                    difference() {
                        cube([bracingWidth *2, bracingWidth*3, boxHeight]);
                        translate([bracingWidth, bracingWidth, 0])
                        cube([bracingWidth, bracingLen, boxHeight]);
                    }
                }
                translate([0,bracingWidth * 3,0]){
                    minkowski() {
                        cube([controllerWidth,  controllerLen, controllerBoxHeight -      roundCornerHeight]);
                        cylinder(roundCornerHeight, roundCornerRadius, roundCornerRadius, $fn=50);
                    }
                }
                
               
                //usb additional wall
                leftOffset = -trrsOutterDiameter - controllerWallWidth - bracingWidth *2;
                width = bracingOuterSize  - bracingWidth * 2 - bracingWidth *2;
                color("green")
                translate([-trrsOutterDiameter ,-controllerWallWidth, 0])
                    cube([width, bracingWidth *3, controllerBoxHeight]);
                
                //top wall
               
                translate([leftOffset, -bracingWidth *3 - controllerWallWidth, controllerBoxHeight])
                    cube([bracingOuterSize, controllerWallWidth, (boxHeight- controllerBoxHeight)]);
                
                if (isExternalResetButtonEnabled) {
                    //button holder
                    holderWidth = buttonWidth + 1;
                    translate([leftOffset + bracingOuterSize - bracingWidth * 2 - holderWidth, - bracingWidth * 3 + buttonDepth , controllerBoxHeight])
                       cube([holderWidth, buttonDepth, buttonAdditionalHeight]);
                }
            }
            // Microcontroller hole
            extraYOffset =  1;
            translate([0, extraYOffset + bracingWidth * 3, controllerBottomHeight])
        cube([controllerWidth,  controllerLen - extraYOffset, boxHeight]);
            
            holesFaceOffset = 2 + bracingWidth * 3;
            // Right wiring hole
            translate([0, holesFaceOffset, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen -holesFaceOffset, controllerBottomHeight + 0.5]);
            
            // Left wiring hole
            translate([controllerWidth - controllerWiringHoleWidth, holesFaceOffset, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen- holesFaceOffset, controllerBottomHeight + 0.5]);
            
            //usb hole
            usbHoleLeftOffset = controllerWidth / 2 - usbHoleWidth / 2;
            translate([usbHoleLeftOffset , -controllerWallWidth  , usbHoleTopOffset + controllerBottomHeight + controllerHeight]) usbCableHole();
            
            usbConnectorLeftOffset = controllerWidth / 2 - usbConnectorWidth / 2;
            translate([usbConnectorLeftOffset, bracingWidth * 3 , usbHoleTopOffset + controllerBottomHeight + controllerHeight]) usbHole();
            
            // controller small hole
            translate([0, bracingWidth * 3, controllerBottomHeight]) cube([controllerWidth, 1, controllerHeight])
            
            if (isExternalResetButtonEnabled) {
                buttonBottomOffset = 0.5;
                //switcher button hole
                leftOffset = -trrsOutterDiameter - controllerWallWidth - bracingWidth *2;
                translate([leftOffset + bracingOuterSize -  bracingWidth * 3 - (buttonWidth - buttonDiameter)/2,  -  bracingWidth * 3 - controllerWallWidth,  buttonDiameter/2 + controllerBoxHeight + buttonBottomOffset])
                rotate([-90, 0, 0]) {
                    cylinder(controllerWallWidth/2, buttonClickDiameter/2, buttonClickDiameter/2, $fn = 50);
                    translate([0, 0, controllerWallWidth/2]) cylinder(controllerWallWidth/2, buttonDiameter/2, buttonDiameter/2, $fn = 50);
                }
                //switcher case hole
                translate([leftOffset + bracingOuterSize -  bracingWidth * 3 - buttonWidth/2 - controllerWallWidth, -  bracingWidth * 3, controllerBoxHeight - controllerWallWidth + buttonBottomOffset]) {
                    cube([buttonWidth, buttonDepth, buttonHeight]);
                }   
            }
            // trrs hole
            trrsHole();

            if (isTmp) {
                translate([-5, 1, controllerBottomHeight]) #cube([controllerWidth + 10, 20, 20]);
            }
        }
    }
}

module trrsHole() {
     // trrs hole
    translate([-trrsOutterDiameter - 1 * controllerWallWidth, - controllerWallWidth , 0]){
        
        translate([trrsOutterDiameter/2 + controllerWallWidth, 0, trrsOutterDiameter/2 + controllerBottomHeight])
        rotate([-90,0,00]) {
        union() {
            translate([0, 0, 0]) {
                cylinder(10, trrsDiameter/2, trrsDiameter/2, $fn=30);
            }
            translate([0, 0, controllerWallWidth]) {
                cylinder(20, trrsOutterDiameter / 2, trrsOutterDiameter / 2, $fn = 30);
            }
        }
    }
    }
}

module bracingLeft() {
    //left
    translate([-trrsOutterDiameter - bracingWidth *2, -controllerWallWidth , 0]) {
        difference() {
            cube([bracingWidth *2, bracingWidth*3, boxHeight]);
             translate([0, bracingWidth, 0])
            cube([bracingWidth, bracingLen, boxHeight]);
        }
    }
}

controllerBox();
if(!isTmp){
    trrsBox();
    //trrs();
    
}
