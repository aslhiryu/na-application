package neoatlantis.applications.web;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neoatlantis.applications.web.listeners.ApplicationListener;
import neoatlantis.applications.web.utils.ResourcesLoader;
import neoatlantis.utils.captcha.interfaces.CaptchaPainter;
import neoatlantis.utils.data.interfaces.ConfirmationCode;
import org.apache.log4j.Logger;

/**
 * Servlet que apoya para obtener un archivo de imagen JPEG, apoyendose en el 
 * {@link neoatlantis.utilidades.accessController.captcha.interfaces.CaptchaPainter Dibujardor de Captchas}
 * que se tenga configurado a partir del {@link neoatlantis.utilidades.accessController.AccessControllerPublisher}.
 * @author Hiryu (aslhiryu@gmail.com)
 * @version 1.0
 */
public class CaptchaImageServlet  extends HttpServlet {
    /**
     * Logeador de la clase
     */
    static final Logger logger = Logger.getLogger(CaptchaImageServlet.class);

    private ConfirmationCode codigo;
    private CaptchaPainter captcha;

    /**
     * M&eacuet;todo que inicializa al servlet, dentro del cual se genera el entorno para 
     * poder desplegar los captchas.
     */
    @Override
    public void init(){
        if( this.getServletContext().getAttribute(ApplicationListener.CAPTCHA_PAINTER_KEY)!=null ){
            this.captcha=(CaptchaPainter)this.getServletContext().getAttribute(ApplicationListener.CAPTCHA_PAINTER_KEY);
        }
        else{
            logger.warn("No se ha definido un 'CaptchaPainter'");
        }
        if( this.getServletContext().getAttribute(ApplicationListener.CODE_GENERATOR_KEY)!=null ){
            this.codigo=(ConfirmationCode)this.getServletContext().getAttribute(ApplicationListener.CODE_GENERATOR_KEY);
        }
        else{
            logger.warn("No se ha definido un 'CaptchaPainter'");
        }
    }

    /**
     * M&eacuet;todo que se ejecuta cuando se invoca al servlet de la forma GET.
     * @param request Petici�&oacute;n de la p&aacute;gina
     * @param response Respuesta de la  p&aacute;gina
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        this.doPost(request, response);
    }

    /**
     * M&eacuet;todo que se ejecuta cuando se invoca al servlet de la forma POST.
     * @param request Petici�&oacute;n de la p&aacute;gina
     * @param response  Respuesta de la  p&aacute;gina
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        int nRead;
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();
        
        logger.debug("Intento pintar el captcha");

        try{
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            response.setHeader("Content-Disposition", " filename=\"captcha.jpg");

            //valido si se cuenta con un painter
            if( this.captcha!=null && this.codigo!=null ){
                logger.debug("Pinto el captcha con: "+this.captcha.getClass());
                logger.debug("Genero el codigo con: "+this.codigo.getClass());
                //almaceno el valor en la sesion
                request.getSession().setAttribute(ApplicationListener.CODE_CONFIRMATION_KEY, this.codigo.create());
                logger.debug("Prepara codigo de confirmacion: "+request.getSession().getAttribute(ApplicationListener.CODE_CONFIRMATION_KEY));
                Image im=this.captcha.paint((String)request.getSession().getAttribute(ApplicationListener.CODE_CONFIRMATION_KEY));
                BufferedImage i=new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g2d = i.createGraphics();
                
                g2d.drawImage(im, new AffineTransform(), null);
                g2d.dispose();

                Iterator writers = ImageIO.getImageWritersByFormatName("jpeg");
                ImageWriter writer = (ImageWriter)writers.next();

                writer.setOutput(ImageIO.createImageOutputStream(response.getOutputStream()));
                writer.write(i);
            }
            else{
                logger.debug("No existe captcha definido");
                
                //si no se tiene definido el Painter de captcha  regreso una imagen genererica d eerror
                InputStream file=ResourcesLoader.class.getResourceAsStream("noCaptcha.png") ;
                byte[] data = new byte[16384];
                while( (nRead = file.read(data, 0, data.length))!=-1 ){
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                buffer.flush();
                response.getOutputStream().write(buffer.toByteArray());
            }

            response.getOutputStream().flush();
        }
        catch(Exception ex){
            logger.error("Error al generar la imagen", ex);
        }
    }
}