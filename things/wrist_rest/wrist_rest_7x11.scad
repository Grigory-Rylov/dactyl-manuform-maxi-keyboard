// Для маленькой подушечки 7x11
height = 3;
angle = -5;
magnetDiameter = 10.1;
magnetDepth = 4;

connectionOffset1 = 5;
connectionLen1 = 42 - 4.5 + connectionOffset1;
connectionLen2 = 34;

shouldUseConnections = true;
 
// Крепление
width1 = 8;
width2 = 5;
height = 6;
outerDiameter = 13;


verticalOffset = 4 + height;

globalOffsetX = 14;
globalOffsetY = 16;


module pad() {
    translate([globalOffsetX, globalOffsetY, 0]) rotate([0, angle, 0]) translate([0, 0, height]) import ("wrist_rest_7x11_body.stl");
}

module hole() {
     translate([globalOffsetX, globalOffsetY, 0]) #import ("wrist_rest_7x11_hole.stl");
}

module mount() {
    extraSize = 0;
    translate([0, height/2, 0])
    linear_extrude(height = outerDiameter / 2, center=true) 
        polygon([[width1/2 - extraSize, height/2], [width2/2 - extraSize, -height/2], [-width2/2 + extraSize, -height/2], [-width1/2 + extraSize, height/2]]);
}

module mountHole() {
    extraSize = 0.1;
    translate([0, height/2, 0])
    linear_extrude(height = outerDiameter, center=true) 
        polygon([[width1/2 + extraSize, height/2], [width2/2 + extraSize, -height/2], 
    [-width2/2 - extraSize, -height/2], [-width1/2 - extraSize, height/2]]);
}

module connection1() {
    offsetY = 100;
    offsetX = -30;
    zAngle = 10;
    
    rotate([0,0,180 - zAngle]) difference () {
      union () {
          //connection1
          translate ([offsetX + 57.323170855986945, offsetY - 52.3453382149892, verticalOffset]) {
            difference () {
              rotate ([0.0,90.0,90.0]) {
                translate ([0, 0, - connectionLen1/2]) {
                  cylinder ($fn=100, h=connectionLen1, r= outerDiameter / 2, center=true);
                }
              }
              rotate ([0.0,90.0,90.0]) {
                 cylinder ($fn=100, magnetDepth, r = magnetDiameter /2, center=true);
                  //cylinder ($fn=100, h=12, r=3/2, center=true);
              }
            }
            // mount
            translate([0,-connectionLen1, -outerDiameter / 4]) rotate([0,0, 180]) mount();
            
            // bottom plate
            difference() {
                translate ([-outerDiameter / 2, -connectionLen1, - outerDiameter/2]) cube([outerDiameter, connectionLen1, outerDiameter /2 ]);
                
                  rotate ([0.0,90.0,90.0]) { 
                      translate ([0, 0, - connectionLen1/2]) {
                        cylinder ($fn=100, h=connectionLen1, r= outerDiameter / 2, center=true);
                      }
                }
            }
          }
      }
    }
}

module connection2() {
    offsetY = 100;
    offsetX = -30;
    zAngle = 10;
    
    rotate([0,0,180 - zAngle]) difference () {
      union () {
      //connection 2
      translate ([offsetX + 24.276834822499474, offsetY - 59.30185901031496, verticalOffset]) {
        difference () {
          rotate ([0.0,90.0,90.0]) {
            translate ([0, 0, -connectionLen2/2]) {
              cylinder ($fn=100, h=connectionLen2, r=outerDiameter / 2, center=true);
            }
          }
          rotate ([0.0,90.0,90.0]) {
            union () {
              cylinder ($fn=100, h=magnetDepth, r=magnetDiameter / 2, center=true);
              //cylinder ($fn=100, h=12, r=3/2, center=true);
            }
          }
        }
        // mount
        translate([0,-connectionLen2, -outerDiameter / 4]) rotate([0,0, 180]) mount();
      
        // bottom plate
        difference() {
                translate ([-outerDiameter / 2, -connectionLen2, - outerDiameter/2]) cube([outerDiameter, connectionLen2, outerDiameter /2 ]);
                
                  rotate ([0.0,90.0,90.0]) { 
                      translate ([0, 0, - connectionLen2/2]) {
                        cylinder ($fn=100, h=connectionLen2, r= outerDiameter / 2, center=true);
                      }
                }
        }
    }
      }
    }   
}

module connections() {
    connection1();
    connection2();
}

module connectionsHoles() {
    offsetY = 100;
    offsetX = -30;
    zAngle = 10;
    
    connectionLen1 = 18;
    connectionLen2 = 10;
    
    rotate([0,0,180 - zAngle]) difference () {
      union () {
        union () {
            //connection1
          translate ([offsetX + 57.3, offsetY - 52.3 - connectionOffset1, verticalOffset]) {
              rotate ([0.0,90.0,90.0]) {
                translate ([0, 0, - connectionLen1/2]) {
                    cylinder ($fn=100, h=connectionLen1, r=outerDiameter/2, center=true);
                    translate([outerDiameter/2, 0, 0]) cube([outerDiameter, outerDiameter, connectionLen1], center=true);
                }
              }
             
             // mount
             translate([0,-connectionLen1,  -outerDiameter / 2]) rotate([0,0, 180]) mountHole();
          }
          //connection 2
          translate ([offsetX + 24.3, offsetY - 59.3, verticalOffset]) {
              rotate ([0.0,90.0,90.0]) {
                translate ([0, 0, -connectionLen2/2]) {
                    translate([outerDiameter/2, 0, 0]) cube([outerDiameter, outerDiameter, connectionLen2], center=true);
                    cylinder ($fn=100, h=connectionLen2, r=outerDiameter/2, center=true);
                }
              }
            
             // mount
             translate([0,-connectionLen2, -outerDiameter / 2]) rotate([0,0, 180]) mountHole();
          }
        }
      }
    
      
    }
}

module wristRestRight() {
    difference() {
        linear_extrude(height = 40)  projection () pad();
        
        translate([0,0,height + 38]) rotate([0, angle, 0]) cube ([200,200, 50], true);
        
        translate([globalOffsetX, globalOffsetY, 2]) translate([0,0,height + 8]) rotate([0, angle, 0]) import("wrist_rest_7x11_hole.stl");

        if (shouldUseConnections) {
            connectionsHoles();
        }
    };
}

wristRestRight();
//connection1();
//connection2();
//translate([0,0,height/2]) cube([200, 200, height], true);

