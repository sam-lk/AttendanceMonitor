/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Attendance;

/**
 *
 * @author Harsha
 */
public class AttendanceCounts {

    private int TotalEmps = 0;
    private int TotalIn = 0;
    private int TotalOut = 0;
    private int PermanentIn = 0;
    private int PermanentOut = 0;
    private int NonPermanentIn = 0;
    private int NonPermanentOut = 0;

    private int PresentTotal = 0;
    private int PresentPermanent = 0;
    private int PresentNonPermanent = 0;

    private int AbsentTotal = 0;
    private int AbsentPermanent = 0;
    private int AbsentNonPermanent = 0;

    public int getTotalEmps() {
        return TotalEmps;
    }

    public void setTotalEmps(int TotalEmps) {
        this.TotalEmps = TotalEmps;
    }
    
    public int getTotalIn() {
        return TotalIn;
    }

    public void setTotalIn(int TotalIn) {
        this.TotalIn = TotalIn;
    }

    public int getTotalOut() {
        return TotalOut;
    }

    public void setTotalOut(int TotalOut) {
        this.TotalOut = TotalOut;
    }

    public int getPermanentIn() {
        return PermanentIn;
    }

    public void setPermanentIn(int PermanentIn) {
        this.PermanentIn = PermanentIn;
    }

    public int getPermanentOut() {
        return PermanentOut;
    }

    public void setPermanentOut(int PermanentOut) {
        this.PermanentOut = PermanentOut;
    }

    public int getNonPermanentIn() {
        return NonPermanentIn;
    }

    public void setNonPermanentIn(int NonPermanentIn) {
        this.NonPermanentIn = NonPermanentIn;
    }

    public int getNonPermanentOut() {
        return NonPermanentOut;
    }

    public void setNonPermanentOut(int NonPermanentOut) {
        this.NonPermanentOut = NonPermanentOut;
    }

    public int getPresentTotal() {
        return PresentTotal;
    }

    public void setPresentTotal(int PresentTotal) {
        this.PresentTotal = PresentTotal;
    }

    public int getPresentPermanent() {
        return PresentPermanent;
    }

    public void setPresentPermanent(int PresentPermanent) {
        this.PresentPermanent = PresentPermanent;
    }

    public int getPresentNonPermanent() {
        return PresentNonPermanent;
    }

    public void setPresentNonPermanent(int PresentNonPermanent) {
        this.PresentNonPermanent = PresentNonPermanent;
    }

    public int getAbsentTotal() {
        return AbsentTotal;
    }

    public void setAbsentTotal(int AbsentTotal) {
        this.AbsentTotal = AbsentTotal;
    }

    public int getAbsentPermanent() {
        return AbsentPermanent;
    }

    public void setAbsentPermanent(int AbsentPermanent) {
        this.AbsentPermanent = AbsentPermanent;
    }

    public int getAbsentNonPermanent() {
        return AbsentNonPermanent;
    }

    public void setAbsentNonPermanent(int AbsentNonPermanent) {
        this.AbsentNonPermanent = AbsentNonPermanent;
    }

}
