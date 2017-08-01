package com.example.hans.openglesproject;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by hans on 2017/8/1.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";
    /**
     * 对于我们定义过的单一顶点，顶点着色器都会被调用一次
     * <p>
     * 当他被调用的时候，他会在a_Position属性里接受当前顶点的位置，这个属性被定义为vec4类型
     * <p>
     * 一个vec4类型包含4个分量的；
     */
    public final static String SIMPLE_VERTEX_SHADER = "" +
            "attribute vec4 a_Position;" + //attribute 是将顶点的属性放入到着色器的手段
            "void main()" +//main 着色器入口
            "{" +
            "gl_Position = a_Position;" +
            "gl_PointSize = 10.0;"+
            "}";//顶点着色器

    /**
     * 片段着色器 片段着色器的主要目的：告诉GPU每个片段最终的颜色是什么
     * <p>
     * u_Color也是一个四分量向量，分别对应红、绿、蓝、Alpha
     */
    public final static String FRAGMENT_SHADER = "" +
            "precision mediump float;" +//lowp mediump highp 分别代表 低精度、中精度、高精度  在顶点着色器中，默认精度为高精度（因为位置十分重要）
            "uniform vec4 u_Color;" +
            "void main()" +
            "{" +
            "gl_FragColor = u_Color;" + //赋值给gl_GragColor OpenGL会使用该颜色作为当片段的对中颜色
            "}";


    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjId = GLES20.glCreateShader(type);//创建着色器对象
        if (shaderObjId == 0) {
            Log.e(TAG, "compileShader: create shader error");
            return 0;
        } else {
            GLES20.glShaderSource(shaderObjId, shaderCode);
            GLES20.glCompileShader(shaderObjId);
            final int[] compileStatus = new int[1];
            //获取编译结果
            GLES20.glGetShaderiv(shaderObjId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);//最后一个参数的意思是，将结果写入到数组的第0个元素

            if (compileStatus[0] == 0) {//编译失败
                Log.e(TAG, "compileShader: compile shader error");
                String shaderInfo = GLES20.glGetShaderInfoLog(shaderObjId);//获取着色器的内容 用于打印
                Log.i(TAG, "compileShader: error info = " + shaderInfo);
                GLES20.glDeleteShader(shaderObjId);
                return 0;
            }
            return shaderObjId;
        }
    }

    /**
     * 一个OpenGL程序就是把一个顶点着色器和一个片段着色器链接在一起 变成单个对象
     * <p>
     * 片段着色器： 负责如何绘制每个点、线、三角形的片段
     * <p>
     * 顶点着色器； 负责确定绘制的位置
     * <p>
     * 顶点、片段着色器通常会一起工作，最终生成图像
     *
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = GLES20.glCreateProgram();//创建OpenGL程序
        if (programObjectId == 0) {
            Log.e(TAG, "linkProgram: create program error");
            return 0;
        } else {
            GLES20.glAttachShader(programObjectId, vertexShaderId);//将顶点着色器附加到程序对象上
            GLES20.glAttachShader(programObjectId, fragmentShaderId);//将片段着色器附加到程序对象上
            //链接程序
            GLES20.glLinkProgram(programObjectId);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                Log.e(TAG, "linkProgram: link program error");
                String shaderInfo = GLES20.glGetProgramInfoLog(programObjectId);//获取着色器的内容 用于打印
                Log.i(TAG, "linkProgram: error info = " + shaderInfo);
                GLES20.glDeleteProgram(programObjectId);
                return 0;
            }
            return programObjectId;
        }
    }

    /**
     * 用于验证当前O喷GL程序状态是否有效的
     *
     * @param programObjectId
     * @return
     */
    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        return validateStatus[0] != 0;
    }
}
