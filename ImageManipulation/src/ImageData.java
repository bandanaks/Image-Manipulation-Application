import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;


/**

Class ImageData is a base class which
respresents image data and the methods for
producing the corresponding wavelet image,
as well as methods to access both of these
datas. </p>

@author L. Grewe
@version 0.0a Feb. 1999
*/

//Note: extends Component to inherit its createImage() method
class ImageData extends Component
{    boolean verbose = false;
	

     //File where data stored and format
     String filename = ""; 
     String format   = "";

 
     
     // Num Rows, columns
     public int rows=0, cols=0;

     //image data
     public int data[];    //TIP: MAYBE CHANGING THIS TO USE java.awt.Image WOULD MAKE YOUR CODING EASIER
     public float minDataRange = Float.MAX_VALUE;
     public float maxDataRange = Float.MIN_VALUE;

     // contains the current image being displayed
     //ImageData ActiveImage; /*= ImageApplication.img_data;*/
     //static Image ActiveI = ActiveImage.createImage();
     
     
     // contains the current image but before the last processing operation was selected
     //ImageData BackupImage; /*= ImageApplication.img_data; */
     //static Image BackupI = BackupImage.createImage();


     

    //**METHODS: for image data*/
     int getData(int row, int col)
      { if (row < rows && col <cols )
            return data[(row*cols)+col];
        else
            return 0;
      }


      int getDataForDisplay(int row, int col)
      {   if (row < rows && col <cols )
            return data[(row*cols)+col];
        else
            return 0;
      }


      void setData(int row, int col, int value)
      {  data[(row*cols)+col] = (int) value;

      }



     

  /**
   * Constructs a ImageData object using the
   * specified by an instance of java.awt.Image,
   * format, and size indicated by numberRows and
   * numberColumns.
   * @param img an Image object containing the data.
   * @param DataFormat the format of the data
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   * @exception IOException if there is an error during
   *  reading of the rangeDataFile.
   */
   public ImageData(Image img, String DataFormat,
                    int numberRows, int numberColumns) throws IOException
     {
      int pixel, red, green, blue, r,c;
      format = DataFormat;
      rows = numberRows;
      cols = numberColumns;
      PixelGrabber pg;

      //From the image passed retrieve the pixels by
      //creating a pixelgrabber and dump pixels
      //into the data[] array.
      data = new int[rows*cols];
      pg = new PixelGrabber(img, 0, 0, cols, rows, data, 0, cols);   //SPECIAL NOTE: you could change so stores in java.awt.Image instead
      try {
          pg.grabPixels();   //this actually gets the pixel data and puts it into the array.
      } catch (InterruptedException e) {
          System.err.println("interrupted waiting for pixels!");
          return;
      }


      //Convert the PixelGrabber pixels to greyscale
      // from the {Alpha, Red, Green, Blue} format 
      // PixelGrabber uses.
      for(r=0; r<rows; r++)
      for(c=0; c<cols; c++)
        {   pixel = data[r*cols + c];
	        red   = (pixel >> 16) & 0xff;
            green = (pixel >>  8) & 0xff;
            blue  = (pixel      ) & 0xff;
            if(verbose)
                System.out.println("RGB: " + red + "," + green +"," +blue);
            data[r*cols+c] = (int)((red+green+blue)/3);  //SPECIAL NOTE: This sample code converts RGB image to a greyscale one
            if(verbose)
                System.out.println("Pixel: " + (int)((red+green+blue)/3));
            minDataRange = Math.min(minDataRange, data[r*cols+c]);
            maxDataRange = Math.max(maxDataRange, data[r*cols+c]);
           
        }      
      
	        
     
          
     
		//{{INIT_CONTROLS
		setBackground(java.awt.Color.white);
		setSize(0,0);
		//}}
	}
  
   
   
  /**
   * Constructs a ImageData object using the
   * specified  size indicated by
   * numberRows and numberColumns that is EMPTY.
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   */
   public ImageData(int numberRows, int numberColumns){

      rows = numberRows;
      cols = numberColumns;
      
     

   }
   
   
   
   /**
   * Constructs a ImageData object using the
   * specified  size indicated by
   * numberRows and numberColumns.  Fill the data[]
   * array with the information stored in
   * the ImageData instance ID, from the 2D
   * neighborhood starting at the upper-left coordinate
   * (rStart,cStart) 
   * @param numberRows the number of rows of data
   * @param numberColumns the number of columns of data
   * @param ID image data to copy data from
   * @param rStart,cStart  Start of Neighborhood copy
   */
   public ImageData(int numberRows, int numberColumns, ImageData ID,
                    int rStart,int cStart){


      //saftey check: Retrieval in ID outside of boundaries
      if(ID.rows<(rStart+numberRows) || ID.cols<(cStart+numberColumns))
      {  rows = 0;
         cols = 0;
         return;
      }   
      
      
      rows = numberRows;
      cols = numberColumns;
      
      //create data[] array.
      data = new int[rows*cols];
      
      //Copy data from ID.
      for(int i=0; i<rows; i++)
      for(int j=0; j<cols; j++)
        {   data[i*cols+j] = ID.data[(rStart+i)*ID.cols + j + cStart];
            minDataRange = Math.min(minDataRange, data[i*cols+j]);
            maxDataRange = Math.max(maxDataRange, data[i*cols+j]);
        }    
      
      
   }   

   

//METHODS
 
   /**
    * brightens the image by brightening each pixel value in the 
    * image by the value passed as a parameter
    * @param x
    */
   public void brighten(int x)
   {
	   for (int i=0; i<rows; i++)
		   for(int j=0; j<cols; j++)
		   {
			   data[i*cols+j] = data[i*cols+j] + x;
			   
			   if(data[i*cols+j] > 255)
				   data[i*cols+j] = 255;
		   } 
   }
   
   
   /**
    * This function is called when the threshold option is 
    * clicked from the process menu.
    * @param t
    */
   public void threshold(int t)
   {
	   int threshold = 100;
	   for (int i=0; i<rows; i++)
		   for (int j=0; j<cols; j++)
		   {
			   if (data[i*cols+j] < threshold)
				   data[i*cols+j] = 0;
			   else
				   data[i*cols+j] = 255;
		   }
   }
   
   /**
    * This function is called when the negative option is 
    * clicked from the process menu.
    */
   public void negative()
   {
	   for (int i=0; i<rows; i++)
		   for (int j=0; j<cols; j++)
		   {
			   data[i*cols+j] = 255 - data[i*cols+j];
		   }
   }
   
   
   /**
    * This function is called when the edge detect option is 
    * clicked from the process menu.
    * @param img
    * @return an image that has been edge detected
    */
   public Image edgeDetect(Image img)
   {
	  /* float xTemplate [] = {-1,0,1,-2,0,2,-1,0,1};
	   float yTemplate [] = {1,2,1,0,0,0,-1,-2,-1};
	   for (int i=0; i<rows; i++)
		   for (int j=0; j<cols; j++)
		   {
			   data[i*cols+j] = data[i*cols+j] * xTemplate[-1];
		   } */
	   
	   int height = img.getHeight(this);
	   int width = img.getWidth(this);
	   
	   for (int i=0; i<data.length-1; i++)
	   {
		   try{
			   int a = data[i] & 0x000000ff;
			   int b = data[i+1] & 0x000000ff;
			   int c = data[i+2] & 0x000000ff;
			   int d = data[i+ width] & 0x000000ff;
			   int e = data[i + width + 2] & 0x000000ff;
			   int f = data[i + 2*width] & 0x000000ff;
			   int g = data[i + 2*width + 1] & 0x000000ff;
			   int h = data[i + 2*width + 2] & 0x000000ff;
			   
			   int horizontal = (a+d+f) - (c+e+h);
			   
			   if (horizontal < 0)
				   horizontal =- horizontal;
			   
			   int vertical = (a+b+c) - (f+g+h);
			   
			   if (vertical < 0)
				   vertical =- vertical;
			   
			   short edge = (short) (10*(horizontal + vertical));
			   edge = (short) (edge+10);
			   
			   if (edge > 255)
				   edge = 255;
			   
			   data[i] = 0xff000000 | edge << 16| edge << 8 | edge;
			   
			   if (data[i+3] % data[i] == 0)
			   {
				   data[i] = 0;
				   data[i+1] = 0;
				   data[i+2] = 0;
				   
				   i += 3;
			   }
		   } catch (Exception e) {
			   
		   }
		 
	   }
	   
	   img = createImage();
	   return img;
	   
   }
   
   
   /**
    * This function is called when the contrast stretch option is 
    * clicked from the process menu.
    * @param x
    */
   public void contrastStretch(int x)
   {
	   
	   int pixel, red, green, blue, r,c;
   
	   for(r=0; r<rows; r++)
		      for(c=0; c<cols; c++)
		        {   pixel = data[r*cols + c];
			        red   = (pixel >> 16) & 0xff;
		            green = (pixel >>  8) & 0xff;
		            blue  = (pixel      ) & 0xff;
		           
		            
		            int min = 0;
		            int max = x;
		            
		            red = (int) (1.0*(red - min) / (max - min) * 255);
		            green = (int) (1.0*(green - min) / (max - min) * 255);
		            blue = (int) (1.0*(blue - min) / (max - min) * 255);
		            
		            if (red > 255)
		            	red = 255;
		            if (green > 255) 
		            	green = 255;
		            if (blue > 255) 
		            	blue = 255;
		           
		            if (red < 0) 
		            	red = 0;
		            if (green < 0) 
		            	green = 0;
		            if (blue < 0) 
		            	blue = 0; 
		            
		           /* if (red > 255) red = 255;
		            else if (red < 0) red = 0;
		            else
		            	red = x;
		            
		            if (green > 255) green = 255;
		            else if (green < 0) green = 0;
		            else
		            	green = x;
		            
		            if (blue > 255) blue = 255;
		            else if (blue < 0) blue = 0;
		            else
		            	blue = x; */
		            
		            data[r*cols+c] = (int)(red+green+blue);
		          
		        } 
   }
   
   


  /**
   * creates a java.awt.Image from the pixels stored 
   * in the array data using 
   * java.awt.image.MemoryImageSource
   */
  public Image createImage()
   {
        int pixels[], t;
        pixels = new int[rows*cols];
    
        //translate the data in data[] to format needed
        for(int r=0;r<rows; r++)
        for(int c=0;c<cols; c++)
        {  t = data[r*cols + c];
           if(t == 999) //due to reg. transformation boundaries produced
            { t = 0; }  // see Transform.ApplyToImage() method
           if(t<0) //due to processing
            { t = -t; }
           else if(t>255) //due to processing
            { t = 255; }
           
           pixels[r*cols+c] = (255 << 24) | (t << 16) | (t << 8) | t;
           //note data is greyscale so red=green=blue above (alpha first)
        }
    
        //Now create Image using new MemoryImageSource
        return ( super.createImage(new MemoryImageSource(cols, rows, pixels, 0, cols)));
	
   } 
   
 
   
   
   /**
	 *Stores the data image to a 
	 * a file as COLOR raw image data format
	 */
	public void storeImage(String filename)throws IOException
	{ 
	   
	    int  pixel, alpha, red, green,blue;
	    
	    
	        
        //Open up file	
        FileOutputStream file_output = new FileOutputStream(filename);
        DataOutputStream DO = new DataOutputStream(file_output);
 
 
        //Write out each pixel as integers
        
	
         
        for(int r=0; r<rows; r++)
	    for(int c=0; c<cols; c++) {
            pixel = data[r*cols + c];
	        red = pixel;
            green = pixel;
            blue = pixel;
            if(verbose)//verbose
    	        {System.out.println("value: " + (int)((red+green+blue)/3));
    	         System.out.println("R,G,B: " + red +"," + green +"," + blue); }
	   
 	        DO.writeByte(red);
 	        DO.writeByte(green);
 	        DO.writeByte(blue);
        }	

        //flush Stream
        DO.flush();
        //close Stream
        DO.close();

    }
   
	// moved this method to ImageFrame class -- 4/19/16                             
   /*public void saveToFile(Image image, String filename)
   {
	   try {
	   String filename = ImageApplication.Filename1;
	   File file = new File(filename);
	   
	   //covert the image object to bufferedimage object
	   
	   BufferedImage image = ImageIO.read(file);
	   ImageIO.write(image, "jpg", file);
	   
	   } catch (IOException e) {
		   System.out.println("Could not save file");
		   
	   } */
	   
	  
	 /*  
	   BufferedImage bufferedI = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
	   bufferedI.getGraphics().drawImage(image, 0, 0, null);
	   
	   Graphics g = bufferedI.createGraphics();
	   g.drawImage(image, 0, 0, null);
	   g.dispose();
	   
	   try {
		   ImageIO.write(bufferedI, "jpg", new File(filename));
	   }
	   catch (Exception e) {
		   
	   }
	   
	   
   } */
  
     
 
	//{{DECLARE_CONTROLS
	//}}
}//End ImageData