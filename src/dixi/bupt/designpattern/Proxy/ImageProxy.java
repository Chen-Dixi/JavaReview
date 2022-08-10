package dixi.bupt.designpattern.Proxy;

public class ImageProxy implements Image {
    private  HighResolutionImage highResolutionImage;

    public ImageProxy(HighResolutionImage highResolutionImage){
        this.highResolutionImage = highResolutionImage;
    }

    @Override
    public void showImage(){
        while(!highResolutionImage.isLoad()){
            try{
                System.out.println("Temp Image: " + highResolutionImage.getWidth());
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        highResolutionImage.showImage();
    }
}
