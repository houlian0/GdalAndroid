package com.gisinfo.android.shp.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gisinfo.android.core.base.AppPath;
import com.gisinfo.android.lib.base.BaseActivity;
import com.gisinfo.android.lib.base.findview.AFindView;
import com.gisinfo.android.lib.base.findview.FindViewUtils;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 注意：示例程序中未加入动态权限，需要自行去手机设置页面设置
 */
public class MainActivity extends BaseActivity {

    @AFindView(onClick = "clickWrite")
    private Button btnShow1;

    @AFindView(onClick = "clickRead")
    private Button btnShow2;

    @AFindView
    private TextView tvShow;

    private String shpPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FindViewUtils.getInstance(mContext).findViews(this, this);

        File projectFolder = AppPath.getAppProjectFolder(mContext);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        //存放路径
//        shpPath = projectFolder.getPath() + "/TDLYXZ_2012_QYZ.shp";
        shpPath = projectFolder.getPath() + "/test.shp";
    }

    // 点击写SHP按钮
    public void clickWrite(View v) {
        try {
            // 尝试写入shp文件，含属性内容
            writeShp();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 点击读shp按钮
    public void clickRead(View v) {
        tvShow.setText("");
        try {
            readShp();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 读取shp
    private void readShp() throws UnsupportedEncodingException {
        // 注册所有的驱动
        ogr.RegisterAll();
        String encoding = gdal.GetConfigOption("SHAPE_ENCODING", null);
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "NO");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");

        //打开文件
        DataSource ds = ogr.Open(shpPath, 0);
        if (ds == null) {
            System.out.println("打开文件失败！");
            return;
        }
        System.out.println("打开文件成功！");

        // 获取该数据源中的图层个数，一般shp数据图层只有一个，如果是mdb、dxf等图层就会有多个
//        int iLayerCount = ds.GetLayerCount();
        // 获取第一个图层
        Layer oLayer = ds.GetLayerByIndex(0);
        if (oLayer == null) {
            System.out.println("获取第0个图层失败！\n");
            return;
        }

        // 对图层进行初始化，如果对图层进行了过滤操作，执行这句后，之前的过滤全部清空
        oLayer.ResetReading();
        // 通过属性表的SQL语句对图层中的要素进行筛选，这部分详细参考SQL查询章节内容
        //oLayer.SetAttributeFilter("\"NAME99\"LIKE \"北京市市辖区\"");
        // 通过指定的几何对象对图层中的要素进行筛选
        //oLayer.SetSpatialFilter();
        // 通过指定的四至范围对图层中的要素进行筛选
        //oLayer.SetSpatialFilterRect();

        // 获取图层中的属性表表头并输出
        System.out.println("属性表结构信息：");
        tvShow.append("属性表结构信息：\n");
        FeatureDefn oDefn = oLayer.GetLayerDefn();
        int iFieldCount = oDefn.GetFieldCount();
        for (int iAttr = 0; iAttr < iFieldCount; iAttr++) {
            FieldDefn oField = oDefn.GetFieldDefn(iAttr);

            String content = oField.GetNameRef() + ": " +
                    oField.GetFieldTypeName(oField.GetFieldType()) + "(" +
                    oField.GetWidth() + "." + oField.GetPrecision() + ")";
            System.out.println(content);

            tvShow.append(content + "\n");

        }

        // 输出图层中的要素个数
        System.out.println("要素个数 = " + oLayer.GetFeatureCount(0));
        tvShow.append("\n要素个数 = " + oLayer.GetFeatureCount(0) + "");

        Feature oFeature = null;
        // 下面开始遍历图层中的要素
        while ((oFeature = oLayer.GetNextFeature()) != null) {
            System.out.println("当前处理第" + oFeature.GetFID() + "个:\n属性值：");
            tvShow.append("\n当前处理第" + oFeature.GetFID() + "个:\n属性值：");
            // 获取要素中的属性表内容
            for (int iField = 0; iField < iFieldCount; iField++) {
                FieldDefn oFieldDefn = oDefn.GetFieldDefn(iField);
                int type = oFieldDefn.GetFieldType();

                switch (type) { // 只支持下面四种
                    case ogr.OFTString:
                        System.out.println(oFeature.GetFieldAsString(iField) + "\t");
                        tvShow.append(oFeature.GetFieldAsString(iField) + "　");
                        break;
                    case ogr.OFTReal:
                        System.out.println(oFeature.GetFieldAsDouble(iField) + "\t");
                        tvShow.append(oFeature.GetFieldAsDouble(iField) + "　");
                        break;
                    case ogr.OFTInteger:
                        System.out.println(oFeature.GetFieldAsInteger(iField) + "\t");
                        tvShow.append(oFeature.GetFieldAsInteger(iField) + "　");
                        break;
                    case ogr.OFTDate:
//                        oFeature.GetFieldAsDateTime();
                        break;
                    default:
                        System.out.println(oFeature.GetFieldAsString(iField) + "\t");
                        tvShow.append(oFeature.GetFieldAsString(iField) + "　");
                        break;
                }
            }

            // 获取要素中的几何体
            Geometry oGeometry = oFeature.GetGeometryRef();
            tvShow.append("\n空间坐标：" + oGeometry.ExportToWkt());
        }

        System.out.println("数据集关闭！");

    }

    // 写入shp文件
    private void writeShp() throws UnsupportedEncodingException {

        ogr.RegisterAll();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "NO");
        gdal.SetConfigOption("SHAPE_ENCODING", "UTF-8");

        String strDriverName = "ESRI Shapefile";
        org.gdal.ogr.Driver oDriver = ogr.GetDriverByName(strDriverName);
        if (oDriver == null) {
            System.out.println(strDriverName + " 驱动不可用！\n");
            return;
        }
        DataSource oDS = oDriver.CreateDataSource(shpPath, null);
        if (oDS == null) {
            System.out.println("创建矢量文件【" + shpPath + "】失败！\n");
            return;
        }

        Layer oLayer = oDS.CreateLayer("TestPolygon", null, ogr.wkbPolygon, null);
        if (oLayer == null) {
            System.out.println("图层创建失败！\n");
            return;
        }

        // 下面创建属性表
        // 先创建一个叫FieldID的整型属性
        FieldDefn oFieldID = new FieldDefn("FieldID", ogr.OFTInteger);
        oLayer.CreateField(oFieldID);

        // 再创建一个叫FeatureName的字符型属性，字符长度为50
        FieldDefn oFieldName = new FieldDefn("FieldName", ogr.OFTString);
        oFieldName.SetWidth(100);
        oLayer.CreateField(oFieldName);

        FeatureDefn oDefn = oLayer.GetLayerDefn();

        // 创建三角形要素
        Feature oFeatureTriangle = new Feature(oDefn);
        oFeatureTriangle.SetField(0, 0);
//        oFeatureTriangle.SetField(1, Base64Utils.encodeStr("三角形11"));
        oFeatureTriangle.SetField(1, new String("三角形11".getBytes(), "UTF-8"));
        Geometry geomTriangle = Geometry.CreateFromWkt("POLYGON ((0 0,20 0,10 15,0 0))");
        oFeatureTriangle.SetGeometry(geomTriangle);
        oLayer.CreateFeature(oFeatureTriangle);

        // 创建矩形要素
        Feature oFeatureRectangle = new Feature(oDefn);
        oFeatureRectangle.SetField(0, 1);
        oFeatureRectangle.SetField(1, "矩形222");
        Geometry geomRectangle = Geometry.CreateFromWkt("POLYGON ((30 0,60 0,60 30,30 30,30 0))");
        oFeatureRectangle.SetGeometry(geomRectangle);
        oLayer.CreateFeature(oFeatureRectangle);

        // 创建五角形要素
        Feature oFeaturePentagon = new Feature(oDefn);
        oFeaturePentagon.SetField(0, 2);
        oFeaturePentagon.SetField(1, "五角形33");
        Geometry geomPentagon = Geometry.CreateFromWkt("POLYGON ((70 0,85 0,90 15,80 30,65 15,70 0))");
        oFeaturePentagon.SetGeometry(geomPentagon);
        oLayer.CreateFeature(oFeaturePentagon);

        try {
            oLayer.SyncToDisk();
            oDS.SyncToDisk();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n数据集创建完成！\n");
    }


}
