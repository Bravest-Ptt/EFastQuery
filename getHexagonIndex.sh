#!/bin/sh

POINT_X=$1
POINT_Y=$2
DISTANCE=$3

echo "dingdian = " + "$POINT_X,$POINT_Y"
X=0
Y=0
PI=1.732
let "X=$POINT_X - $DISTANCE / 2"
let "Y=$POINT_Y + $DISTANCE * $(echo \"($PI / 4)\" | bc)"
echo "jian_zuo = " + "$X, $Y)"
echo "jian_you = " + "$POINT_X + ($DISTANCE / 2), $POINT_Y + ($DISTANCE * (1.732 / 4))"
echo "kua_zuo = " + "$POINT_X - ($DISTANCE / 2), $POINT_Y + ($DISTANCE * (1.732 * 3 / 4))"
echo "kua_you = " + "$POINT_X + ($DISTANCE / 2), $POINT_Y + ($DISTANCE * (1.732 * 3 / 4))"
echo "di = " + "$POINT_X, $POINT_Y + (1.732 * $DISTANCE)"
echo "bianchang = " + "(1.732 / 2) * $DISTANCE"
