package reservation;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private int dayLimit;
    private int numOfFloor;
    private int numOfRoom;
    private int bitmaskNum;

    private long[][][] rooms;

    public RoomManager(int problem) {
        if (problem == 1) {
            dayLimit = 200;
            numOfFloor = 3;
            numOfRoom = 20;
            bitmaskNum = 1;
        } else {
            dayLimit = 1000;
            numOfFloor = 10;
            numOfRoom = 200;
            bitmaskNum = 4;
        }

        rooms = new long[dayLimit + 1][numOfFloor + 1][bitmaskNum];
    }

    public Integer getEmptyRoomNumber(int roomAmount, int checkInDay, int checkOutDay) {
        long roomBitmask1 = getRoomBitmask(roomAmount);
        long roomBitmask2 = ~0L;

        int floor = 1;
        int index = 0;
        int splitIndex = 0;
        boolean splitFlag = false;
        boolean isFound = false;

        for (floor = 1; floor <= numOfFloor; floor++) {
            index = 0;
            splitIndex = 0;
            splitFlag = false;
            roomBitmask2 = ~0;

            while (splitIndex * Long.SIZE + index <= numOfRoom - roomAmount) {
                if (splitFlag) {
                    if ((((roomBitmask1 >>> index) & rooms[checkInDay][floor][splitIndex]) == 0)
                            && ((roomBitmask2 & rooms[checkInDay][floor][splitIndex + 1]) == 0)) {
                        if (checkToCheckOutDay(roomBitmask1, roomBitmask2, floor, index, checkInDay + 1, checkOutDay, splitIndex, true)) {
                            isFound = true;
                            break;
                        }
                    }
                } else {
                    if (((roomBitmask1 >>> index) & rooms[checkInDay][floor][splitIndex]) == 0) {
                        if (checkToCheckOutDay(roomBitmask1, roomBitmask2, floor, index, checkInDay + 1, checkOutDay, splitIndex, false)) {
                            isFound = true;
                            break;
                        }
                    }
                }

                index++;

                if (index + roomAmount > Long.SIZE) {
                    roomBitmask2 = (~0L >>> (index + roomAmount) - Long.SIZE);
                    roomBitmask2 = ~roomBitmask2;
                    splitFlag = true;
                }

                if (index == 64) {
                   splitFlag = false;
                   splitIndex++;
                   roomBitmask2 = ~0L;
                   index = 0;
                }
            }

            if (isFound) {
                break;
            }
        }

        int resultFloor = -1;
        int resultRoomNumber = -1;
        if (isFound) {
            for (int day = checkInDay; day < checkOutDay; day++) {
                if (splitFlag) {
                    rooms[day][floor][splitIndex] = rooms[day][floor][splitIndex] | (roomBitmask1 >>> index);
                    rooms[day][floor][splitIndex + 1] = rooms[day][floor][splitIndex + 1] | roomBitmask2;
                } else {
                    rooms[day][floor][splitIndex] = rooms[day][floor][splitIndex] | (roomBitmask1 >>> index);
                }
            }

            resultFloor = floor;
            resultRoomNumber = splitIndex * Long.SIZE + index + 1;
        }

        if (resultFloor < 0) {
            return null;
        }

        return resultFloor * 1000 + resultRoomNumber;
    }

    private boolean checkToCheckOutDay(long roomBitmask1, long roomBitmask2, int floor, int index, int start, int end, int splitIndex, boolean splitFlag) {
        for (int day = start; day <= end; day++) {
            if (splitFlag) {
                if (!((((roomBitmask1 >>> index) & rooms[day][floor][splitIndex]) == 0)
                        && ((roomBitmask2 & rooms[day][floor][splitIndex + 1]) == 0))) {
                    return false;
                }
            } else {
                if (((roomBitmask1 >>> index) & rooms[day][floor][splitIndex]) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private long getRoomBitmask(int roomAmount) {
        long roomBitmask = ~0L;
        for (int i = 0; i < roomAmount; i++) {
            roomBitmask = (roomBitmask >>> 1);
        }
        roomBitmask = ~roomBitmask;
        return roomBitmask;
    }
}
