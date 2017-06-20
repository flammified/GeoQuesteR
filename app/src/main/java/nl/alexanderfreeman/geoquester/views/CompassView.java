package nl.alexanderfreeman.geoquester.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import nl.alexanderfreeman.geoquester.R;


/* Shamelessly stolen (used ;))
from https://github.com/janzuurbier/KompasWithFragments/blob/master/app/src/main/java/zrb/hu/nl/kompaswithfragments*/

public class CompassView extends View  {
    Bitmap pijl;
    Matrix matrix = new Matrix();
    Paint paint = new Paint();
    float xMidden = 0, yMidden=0;
    private float richting = 0f;

    public CompassView(Context context) {
        super(context);
        pijl = BitmapFactory.decodeResource(getResources(), R.drawable.pijl);
        matrix.setTranslate(-44,-94);
        paint.setColor(0xff000000);
        paint.setTextSize(16);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pijl = BitmapFactory.decodeResource(getResources(), R.drawable.pijl);
        matrix.setTranslate(-44,-94);
        paint.setColor(0xff000000);
    }

    public void setDirection(float f){
        this.richting = f;
        invalidate();
    }

    public void onDraw(Canvas c){
        c.drawColor(0xffffffff);
        //int xMidden = getMeasuredWidth()/2;
        //int yMidden = getMeasuredHeight()/2;
        matrix.postRotate(richting, xMidden, yMidden);
        c.drawBitmap(pijl, matrix, paint);
        matrix.postRotate(-richting, xMidden, yMidden);
    }

    public void onMeasure(int specX, int specY){
        int sizeX = MeasureSpec.getSize(specX);
        int sizeY = MeasureSpec.getSize(specY);
        int modeX = MeasureSpec.getMode(specX);
        int modeY = MeasureSpec.getMode(specY);
        int x = 200;
        int y = 200;
        if(modeX == MeasureSpec.EXACTLY || modeX == MeasureSpec.AT_MOST) x = sizeX;
        if(modeY == MeasureSpec.EXACTLY || modeY == MeasureSpec.AT_MOST) y = sizeY;
        setMeasuredDimension(x, y);
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh){
        matrix.postTranslate(-xMidden,-yMidden);
        xMidden = w/2;
        yMidden = h/2;
        matrix.postTranslate(xMidden,yMidden);
    }

}