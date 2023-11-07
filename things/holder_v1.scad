// microcontroller dimension
controllerWidth = 23;
controllerLen = 53.5;
controllerHeight = 1;

buttonAdditionalHeight = 4;
controllerBoxHeight = 9;
controllerWallWidth = 1;
controllerBottomHeight = 1;

//usb hole
usbHoleTopOffset = 2.2;
usbHoleHeight = 3.7;
usbHoleWidth = usbHoleHeight + 5.6;

// trrs
trrsDiameter = 6; // real size 5.8
trrsLen1 = 4;
trrsLen2 = 11.8;
trrsContactsLen = 6.5;
trrsOutterDiameter = 7.7;

//
controllerWiringHoleWidth = 4;
roundCornerHeight = 1;
roundCornerRadius = 1;

//bracing
bracingWidth = 1.5;
bracingOuterSize = 31.366;

//button
buttonDiameter = 3;
buttonClickDiameter = 1.6;
buttonHeight = 5.2;
buttonWidth = 6;
buttonDepth = 1.5;
 
boxHeight = controllerBoxHeight + buttonAdditionalHeight;

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

module controllerBox() {
    translate([0, 3*bracingWidth, 0])
    difference() {
        union(){
            minkowski() {
                cube([controllerWidth,  controllerLen, controllerBoxHeight -      roundCornerHeight]);
                cylinder(roundCornerHeight, roundCornerRadius, roundCornerRadius, $fn=50);
            }
           
            //usb additional wall
            leftOffset = -trrsOutterDiameter - controllerWallWidth - bracingWidth *2;
            width = leftOffset + bracingOuterSize - bracingWidth *2;
            translate([0,-controllerWallWidth - bracingWidth * 3, 0])
            cube([width, bracingWidth *3, controllerBoxHeight]);
            
            //top wall
            translate([leftOffset, -bracingWidth *3 - controllerWallWidth, controllerBoxHeight])
            cube([bracingOuterSize, controllerWallWidth, (boxHeight- controllerBoxHeight)]);
            
            //button holder
            holderWidth = buttonWidth + 1;
            translate([leftOffset + bracingOuterSize - bracingWidth * 2 - holderWidth, - bracingWidth * 3 + buttonDepth , controllerBoxHeight])
            cube([holderWidth, buttonDepth, buttonAdditionalHeight]);
            
             //left
            translate([-trrsOutterDiameter - controllerWallWidth - bracingWidth *2, -controllerWallWidth -3*bracingWidth, 0]) {
           
                //rigth
                translate([bracingOuterSize - bracingWidth *2, 0, 0])
                difference() {
                    cube([bracingWidth *2, bracingWidth*3, boxHeight]);
                    translate([bracingWidth, bracingWidth, 0])
                    cube([bracingWidth, bracingWidth, boxHeight]);
                }
            }
        }
        // Microcontroller hole
        translate([0,0, controllerBottomHeight])
    cube([controllerWidth,  controllerLen, boxHeight]);
        
        // Right wiring hole
        translate([0, 0, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen, controllerBottomHeight + 0.5]);
        
        // Left wiring hole
        translate([controllerWidth - controllerWiringHoleWidth, 0, -0.5 ]) cube([controllerWiringHoleWidth, controllerLen, controllerBottomHeight + 0.5]);
        
        //usb hole
        translate([controllerWidth/2 - usbHoleHeight/2 - controllerWallWidth, -controllerWallWidth - 3*bracingWidth, usbHoleTopOffset + usbHoleHeight/2])
        rotate([-90,0,00]) {
            cylinder(10, usbHoleHeight/2, usbHoleHeight/2, $fn=50);
            translate([5.6,0,0]) cylinder(10, usbHoleHeight/2, usbHoleHeight/2, $fn=50);
            translate([0, -usbHoleHeight/2 ,0]) cube([5.6, usbHoleHeight, 10]);
        } 
        
        //usb hole entry
        outerHoleWidth = 14;
        translate([controllerWidth/2 - usbHoleHeight/2 - controllerWallWidth, -controllerWallWidth - 3*bracingWidth, usbHoleTopOffset + usbHoleHeight/2])
         rotate([-90, 0, 0]) {
             hull(){
                 union(){
                    cylinder(3*bracingWidth - controllerWallWidth, usbHoleHeight/2, usbHoleHeight/2, $fn=50);
                    translate([5.6,0,0]) cylinder( 3*bracingWidth - controllerWallWidth, usbHoleHeight/2, usbHoleHeight/2, $fn=50);
                    translate([0, -usbHoleHeight/2 ,0]) cube([5.6, usbHoleHeight, 3*bracingWidth - controllerWallWidth]);
                 }
                 translate([-usbHoleHeight/2 -(outerHoleWidth - usbHoleWidth)/2,- outerHoleWidth/4,0]) cube([outerHoleWidth, outerHoleWidth/2, 1]);
             }
         }
       
        //usb offset
        translate([controllerWidth/2 - usbHoleHeight - controllerWallWidth, -bracingWidth, controllerWallWidth])
        cube([5.6 + usbHoleHeight, bracingWidth, controllerBoxHeight]);
        
        buttonBottomOffset = 0.5;
        //switcher button hole
        leftOffset = -trrsOutterDiameter - controllerWallWidth - bracingWidth *2;
        translate([leftOffset + bracingOuterSize -  bracingWidth * 3 - (buttonWidth - buttonDiameter)/2,  -  bracingWidth * 3 - controllerWallWidth,  buttonDiameter/2 + controllerBoxHeight + buttonBottomOffset])
        rotate([-90, 0, 0]) {
            cylinder(controllerWallWidth/2, buttonClickDiameter/2, buttonClickDiameter/2, $fn = 50);
            translate([0, 0, controllerWallWidth/2]) cylinder(controllerWallWidth/2, buttonDiameter/2, buttonDiameter/2, $fn = 50);
        }
        
        //switcher case hole
        translate([leftOffset + bracingOuterSize -  bracingWidth * 3 - buttonWidth/2 - controllerWallWidth, -  bracingWidth * 3, controllerBoxHeight - controllerWallWidth + buttonBottomOffset])
        cube([buttonWidth, buttonDepth, buttonHeight]);
    }
}

module trrsBox() {
  
    translate([-trrsOutterDiameter - 2 * controllerWallWidth , - controllerWallWidth, 0])
    difference() {
        trrsLen = trrsLen2 + trrsContactsLen + 4;
        cube([trrsOutterDiameter + 2*controllerWallWidth, trrsLen + 2*controllerWallWidth, controllerBoxHeight]);

        // trrs box hole
        translate([controllerWallWidth, controllerWallWidth, controllerBottomHeight])
    cube([trrsOutterDiameter,  trrsLen, controllerBoxHeight]);
     
        // trrs hole
        translate([trrsOutterDiameter/2 + controllerWallWidth, 0, trrsOutterDiameter/2 + controllerBottomHeight])
    rotate([-90,0,00]) {
        translate([0, 0, -trrsLen1 + controllerWallWidth]) {
            cylinder(trrsLen1, trrsDiameter/2, trrsDiameter/2, $fn=50);
            }
        } 
         //back hole
        rotate([-90,0,00]) {
            translate([trrsOutterDiameter/2 + controllerWallWidth, -trrsOutterDiameter/2 - controllerBoxHeight/2 ,trrsLen + controllerWallWidth])
        cylinder(controllerWallWidth, trrsOutterDiameter/2, trrsOutterDiameter/2, $fn=50);
        }
    }
}

module bracingLeft() {
    //left
    translate([-trrsOutterDiameter - controllerWallWidth - bracingWidth *2, -controllerWallWidth , 0]) {
        color("brown")
        difference() {
            cube([bracingWidth *2, bracingWidth*3, boxHeight]);
             translate([0, bracingWidth, 0])
            cube([bracingWidth, bracingWidth, boxHeight]);
        }
    }
}

controllerBox();
trrsBox();
//trrs();
bracingLeft();

