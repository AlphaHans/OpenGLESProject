package com.example.hans.openglesproject;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hans on 2017/7/31.
 */

public class AirHockeyRender implements GLSurfaceView.Renderer {
    //uniform位置
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    //获取属性的位置
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final float[] TABLE_VERICES_WITH_TRIANGLES = {
//           // Triangle 1
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//
//            // Triangle 2
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            0.5f, 0.5f,

            //X Y R 

            // Triangle fan
            0, 0,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            -0.5f, -0.5f,

            //Line 1
            -0.5f, 0f,
            0.5f, 0f,

            //Mallets
            0f, -0.25f,
            0f, 0.25f
    };

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer mVertexData;
    private final Context mContext;
    private int mProgramId;

    public AirHockeyRender(Context context) {
        mContext = context;
        mVertexData = ByteBuffer.allocateDirect(TABLE_VERICES_WITH_TRIANGLES.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexData.put(TABLE_VERICES_WITH_TRIANGLES);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int vertexShader = ShaderHelper.compileVertexShader(ShaderHelper.SIMPLE_VERTEX_SHADER);
        int fragmentShader = ShaderHelper.compileFragmentShader(ShaderHelper.FRAGMENT_SHADER);

        mProgramId = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        //告诉OpenGL在绘制任何东西到屏幕上的时候要使用这里定义的程序
        GLES20.glUseProgram(mProgramId);


        /**
         * 获取位置
         */
        uColorLocation = GLES20.glGetUniformLocation(mProgramId, U_COLOR);//当opengl程序对象链接成功，我们可以开始查询uniform的位置

        aPositionLocation = GLES20.glGetAttribLocation(mProgramId, A_POSITION);//获取属性


        /**
         * 关联属性与顶点数据的数组
         */
        mVertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation
                //每个属性的数据的计算。 每隔顶点使用两个浮点数：一个X一个Y。 这意味着需要两个分量。
                //我们为顶点只传递了两个分量，但在着色器中a_Position被定义为vec4，他有四个分量。 如果一个分量没有被指定，默认情况下，opengl会把前面3个分量设置为0，最后一个设置为1
                , POSITION_COMPONENT_COUNT
                , GLES20.GL_FLOAT//表面数据类型 为float
                , false //只有整形数据的时候，这个参数才有意义
                , 0 //stride 当数组存储多于一个属性的时候，它才有用意义。 如颜色属性
                , mVertexData);
        /**
         * 调用该方法，让OpenGL知道如何寻找它所需要的数据
         */
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


        /**
         * 更新着色器代码中 u_Color的指
         *
         * 与属性不同,uniform分量没有默认值,如果uniform在着色器被定义为vec4 我们就需要所有分量值(4个)
         */
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        /**
         * 指定颜色后开始绘制桌子 读取数组位置0 以及后面的6个数
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);


        /**
         * 绘制分割线
         */
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);


        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }


}
