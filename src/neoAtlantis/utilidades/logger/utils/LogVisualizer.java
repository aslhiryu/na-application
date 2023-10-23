package neoAtlantis.utilidades.logger.utils;

import java.io.*;

/**
 *
 * @author Hiryu (aslhiryu@gmail.com)
 */
public class LogVisualizer {
    private String archivo;
    private long max=200000l;

    public LogVisualizer(String log){
        this.archivo=log;
    }

    public LogVisualizer(String log, long max){
        this(log);
        this.max=max;
    }

    public String getDatos() throws Exception{
        return getDatos(true);
    }

    public String getDatos(boolean limitado) throws Exception{
        return getDatos(limitado, false);
    }

    public String getDatos(boolean limitado, boolean html) throws Exception{
        File f=new File(this.archivo);
        int c;
        String cad;
        boolean linea=false;
        StringBuffer sb=new StringBuffer("");
        //StringBuffer lina=new StringBuffer("");
        
        //FileInputStream fis=new FileInputStream(f);
        BufferedReader br=new BufferedReader(new FileReader(f));

        //revisa el tamaño actual del log
        if( limitado && f.length()>this.max ){
            //fis.skip(f.length()-this.max);
            br.skip(f.length()-this.max);

            //omito los caracteres hasta encontrar la nueva linea
            /*while( (c=fis.read())!=-1 ){
                if( ((char)c)=='\n' || ((char)c)=='\r' ){
                    break;
                }
            }*/
            br.readLine();
        }

        if(html){
            //sb.append("<table border=0 width='100%' class='tabla_log'>\n<tr><td class='log_linea1'>");
            sb.append("<table border=0 width='100%' class='tabla_log'>\n");
        }
        //while( (c=fis.read())!=-1 ){
        while( br.ready() ){
            //valida si es el fin de
            /*if( html && ((char)c)=='\n' ){
                if( !lina.toString().trim().equals("") ){
                    sb.append("</td></tr>\n<tr><td class='").append(linea? "log_linea1": "log_linea2").append("' >").append((char)c);
                    linea=!linea;
                }

                lina=new StringBuffer("");
            }
            else if( ((char)c)=='<' ){
                sb.append( "&lt;" );
            }
            else if( ((char)c)=='>' ){
                sb.append( "&gt;" );
            }
            else{
                sb.append( (char)c );
            }

            lina.append((char)c);*/
            cad=br.readLine();

            if( cad.trim().length()==0 ){
                continue;
            }

            sb.append("<tr><td class='").append(linea? "log_linea1": "log_linea2").append("'>\n");
            sb.append(cad.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("INFO", "<font color='navy'><b>INFO</b></font>").replaceAll("(?i)ERROR", "<font color='red'><b>ERROR</b></font>").replaceAll("WARNING", "<font color='orange'><b>WARNING</b></font>")).append('\n');
            sb.append("</td></tr>\n");
            linea=!linea;
        }
        if(html){
            //sb.append("</td></tr>\n</table>");
            sb.append("</table>\n");
        }

        //fis.close();
        br.close();

        return sb.toString();
    }

    public void resetLog() throws Exception{
        FileOutputStream fos=new FileOutputStream(this.archivo, false);
        fos.write( ((int)' ') );
        fos.close();
    }

}
