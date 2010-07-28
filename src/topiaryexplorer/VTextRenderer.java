//From user V on the processing.org forums
package topiarytool;

import java.awt.*;
import java.text.*; 
import com.sun.opengl.util.*;
import com.sun.opengl.util.j2d.*; 

class VTextRenderer
{

 int _w, _h;
 
 String _fontName;
 int _fontSize;
 TextRenderer _textRender;
 Font font;
 
 
 VTextRenderer( String fontName, int size )
 {
   _fontName = fontName;
   _fontSize = size;
   _textRender = new TextRenderer( new Font(fontName, Font.TRUETYPE_FONT, size), true, true, null, true ); 
   _textRender.setColor( 0.0f, 0.0f, 0.0f, 1.0f );
   //_textRender.setUseVertexArrays( true );
 }

 VTextRenderer( String fontName, int size, boolean antialiased, boolean mipmap )
 {
   _fontName = fontName;
   _fontSize = size;
   _textRender = new TextRenderer( new Font(fontName, Font.TRUETYPE_FONT, size), antialiased, true, null, mipmap ); 
   _textRender.setColor( 0.0f, 0.0f, 0.0f, 1.0f );
   //_textRender.setUseVertexArrays( true );
 }
 
 
 void print( String str, int x, int y )
 {
   _textRender.beginRendering( _w, _h, true );
   _textRender.draw( str, x, y );
   _textRender.endRendering();   
   _textRender.flush();
 }

 void print( String str, float x, float y, float z )
 {
   print( str, x, y, z, 1.0f );
 }

 void print( String str, float x, float y, float z, float s )
 {
   _textRender.begin3DRendering();
   _textRender.draw3D( str, x, y, z, s );
   _textRender.end3DRendering();   
   _textRender.flush();
 }

 void setColor( float c )
 {
   setColor( c, c, c, 1 );
 }

 void setColor( float c, float a )
 {
   setColor( c, c, c, a );
 }

 void setColor( float r, float g, float b )
 {
   setColor( r, g, b, 1 );
 }
 
 void setColor( float r, float g, float b, float a )
 {
   _textRender.setColor( r, g, b, a );
 }
 
 void setSmoothing( boolean flag )
 {
   _textRender.setSmoothing( flag );
 }
 
}
