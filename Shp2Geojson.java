package shp2GeojsonTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

import com.amazonaws.util.json.JSONObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;  
  public class Shp2Geojson {
	   //name1,name2为你要获取的属性值的key
	   public void shape2Geojson(String shpPath, String jsonPath,String name1,String name2 ){  
		   FeatureJSON fjson = new FeatureJSON();	       
		   try{  
	            StringBuffer sb = new StringBuffer();    
	            File file = new File(shpPath);  
	            ShapefileDataStore shpDataStore = null;  	              
	            shpDataStore = new ShapefileDataStore(file.toURL());
	            Charset charset = Charset.forName("UTF-8");  
	            shpDataStore.setCharset(charset);  
	            String typeName = shpDataStore.getTypeNames()[0];  
	            SimpleFeatureSource featureSource = null;  
	            featureSource =  shpDataStore.getFeatureSource (typeName);  
	            SimpleFeatureCollection result = featureSource.getFeatures();  
	            SimpleFeatureIterator itertor = result.features();  
	            JSONObject finaltext =new JSONObject();
	            finaltext.put("type", "FeatureCollection");
	            //构造JSONObject列表
	            List<JSONObject> featuresvalue = new ArrayList<JSONObject>();
	            while (itertor.hasNext())  
	            {  
                	JSONObject pro =new JSONObject();
	                SimpleFeature feature = itertor.next();  
	                List<Object> list=feature.getAttributes();
	                Object obj=list.get(0);
	                Geometry area=((Geometry) obj);
	                int parts=area.getNumGeometries();
	                //features属性内除geometry外的属性的值得添加
	                JSONObject feain = new JSONObject();
                    for(int i=0;i<parts;i++){
                    	//geometry相关数据(JSONObject对象)
                    	JSONObject geo = new JSONObject();
                    	Polygon l=(Polygon)area.getGeometryN(i);
                    	Coordinate[] cs = l.getCoordinates();
                    	//获得key为name1的值
                    	try{
                        String name = (String) feature.getProperty(name1).getValue();
                        pro.put("name", name);
                        }
                    	catch(Exception e){	
                        	System.out.println("对不起"+name1+"找不到值");
                        }
                       //获得key为name2的值
                        try{
                        String pac = (String) feature.getProperty(name2).getValue();
                        pro.put("PAC", pac);
                        }
                        catch(Exception e){	
                        	System.out.println("对不起"+name2+"找不到值");
                        }
                        feain.put("properties", pro);
                        feain.put("type", "Feature");
                        geo.put("type", "Polygon");
                        //coordinates属性的值
                        List<List<List<Double>>> Coor = new LinkedList<List<List<Double>>>();
                        //存放所有点位的信息
                        List<List<Double>> coor2 = new ArrayList<List<Double>>();
                        for(int j=0;j<cs.length;j++){
                        	 Coordinate coor=cs[j];
                        	 //存放每一个点x,y值
                        	 List<Double> coor1 = new ArrayList<Double>();
                        	 coor1.add(coor.x);
                        	 coor1.add(coor.y);
                        	 coor2.add(coor1);
                        }
                        Coor.add(coor2);                        
                        geo.put("coordinates",Coor);
                        feain.put("geometry",geo);
                        featuresvalue.add(feain);
                    }
                    finaltext.put("features", featuresvalue);
	            }
	            itertor.close();
	            //添加类型         
	            finaltext.put("type", "FeatureCollection"); 
	            sb.append(finaltext.toString());  
	            //sb.append("}"); 
	            writeFile(sb.toString(), jsonPath);
 
	        }  
	        catch(Exception e){  
 
	            e.printStackTrace();  
	              
	        }  

	    }  
    
	   public JSONObject toGeoJson(String name,String pac,Coordinate[] cs)
	   {
	           return new JSONObject();
	   } 
	   
	   public void writeFile(String str, String jsonPath)
	    {
	    	 FileOutputStream fos=null;
	    	 OutputStreamWriter oStreamWriter = null;
	    	
			 try {
		         oStreamWriter = new OutputStreamWriter(new FileOutputStream(jsonPath), "utf-8");
		         oStreamWriter.append(str);
		         oStreamWriter.flush();
		         
		         System.out.println("文件已更新!");
		      }catch (IOException obj) {
		    	  System.out.println("创建文件时出错!");
		      }finally{
		    	  try{
		    		  if(fos!=null) {
		    			  
		    			  oStreamWriter.close();
		    		  }
		    	  }catch (IOException e) {
			    	 e.printStackTrace();
		    	  }
		      }
	    }


	public static void main(String[] args) {
			Shp2Geojson shp2Geojson = new Shp2Geojson();  
	        long start = System.currentTimeMillis();  
	        String shpPath = "C:\\Users\\Windows User\\Desktop\\shp转json问题\\舒城简化的shp\\sct.shp";  
	        String jsonPath = "E:\\shucheng.json";
	        //传参在这里
	        //参数包括shp源文件地址,生成json文件的地址,所需获得value的key
	        shp2Geojson.shape2Geojson(shpPath, jsonPath,"XZMC", "XZBM");  	          
	        System.out.println(jsonPath+",消耗的时间是"+(System.currentTimeMillis() - start)+"ms");  
	}

}


