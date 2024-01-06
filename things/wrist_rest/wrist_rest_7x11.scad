// Для маленькой подушечки 7x11
height = 3;
angle = -5;
magnetDiameter = 10.1;
magnetDepth = 4;

connectionOffset1 = 1.5;
connectionLen1 = 42 - 4.5 + connectionOffset1-2;
connectionLen2 = 34;

shouldUseConnections = true;
 
// Крепление
width1 = 8;
width2 = 5;
height = 8;
outerDiameter = 13;


verticalOffset = 4 + height;

globalOffsetX = 0;
globalOffsetY = 0;


module hole(shapeHeight) {
    shapeWidth = 114;
    topCircleHalfHeight = 46;
    rotate([0,0,90]) union(){
        translate([-2, 0, 0]) {
            difference(){
                scale([(topCircleHalfHeight*2/(shapeWidth)), 1, 1]) cylinder(r=shapeWidth/2, h = shapeHeight, $fn=100, center = true);
                translate([topCircleHalfHeight/2,0,0]){ cube([topCircleHalfHeight, shapeWidth, shapeHeight], center = true); }
              
            }
        }
         
        circleRadius = 30;
        delta = (shapeWidth - circleRadius*2*2)/2;
        translate([0, circleRadius + delta, 0]) cylinder(r=30, h = shapeHeight, $fn=100, center = true);
        translate([0, -circleRadius - delta, 0]) cylinder(r   =30, h = shapeHeight, $fn=100, center = true);
        
        rad2 = 16;
        difference() {
            translate([18, 0, 0]) cube([20, 28, shapeHeight], center=true);
            translate([30,0,0]) scale([(6.2*2/(rad2 * 2)),1,1]) cylinder(r=rad2, h=shapeHeight, $fn=100, center = true);
        }
    }
}


module body() {
    minkowski() {
        hole(1);
        cylinder(h = 1, r=2);
    }
}


module pad() {
    //scale([1,1,1]) hole();
    rotate([0, angle, 0]) translate([0, 0, height]) body();
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
    offsetY = 119;
    offsetX = -30;
    zAngle = 10;
    
    connectionLen1 = 18;
    connectionLen2 = 10;
    
    rotate([0,0,180 - zAngle]) difference () {
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

module wristRestRight() {
    difference() {
        linear_extrude(height = 40)  projection () pad();
        
        translate([0,0,height + 38]) rotate([0, angle, 0]) cube ([200,200, 50], true);
        
        translate([0,0, height + 11.5]) rotate([0, angle, 0]) hole(3);

        if (shouldUseConnections) {
            connectionsHoles();
        }
    };
}

//wristRestRight();
connection1();
connection2();
//translate([0,0,height/2]) cube([200, 200, height], true);

