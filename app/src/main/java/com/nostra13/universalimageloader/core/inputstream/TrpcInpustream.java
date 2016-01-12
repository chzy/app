package com.nostra13.universalimageloader.core.inputstream;

import android.util.Log;

import com.chd.Transform.InputTrasnport;
import com.chd.proto.FTYPE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxp1 on 2015/12/13.
 */
public final   class TrpcInpustream extends InputStream {
   private InputTrasnport transport;
   private /*RandomAccessFile*/FileOutputStream outfilewrter=null;
   private String _savefile;
    private int remoteoffset=0;
    private int objlen=-1;


    public TrpcInpustream(String name, String savefile)    {
        transport=new InputTrasnport(name, FTYPE.PICTURE);
        if (savefile==null || savefile.indexOf('.')<0)
            return;
        _savefile=savefile;

    }


    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     * <p/>
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
       // return transport.read(null,0,1);
        throw  new IOException("not support!!");
    }

    @Override
   public int read(byte[] buffer, int byteOffset, int byteCount)  throws IOException {
        Log.d("TrpcInpustream", "offset:" + remoteoffset + " readcount:" + (buffer.length - byteOffset));
        int redbytes=0;
       // int offset=remoteoffset+redbytes;
         redbytes= transport.read(buffer,remoteoffset,byteCount);

        if (redbytes>-1 )
        {
            if (outfilewrter==null)
            try {
                outfilewrter=new FileOutputStream(_savefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("TrpcInputstream",e.getMessage());
            }

            remoteoffset+=redbytes;
            if ( outfilewrter!=null)
            //outfilewrter.seek(byteOffset);
            try {
               /* if (byteCount==1)
                    outfilewrter.write(new byte[]{(byte)redbytes},byteOffset,byteCount);
                else
                    outfilewrter.write(buffer,byteOffset,redbytes);*/
                outfilewrter.write(buffer, byteOffset, redbytes);
            }catch (Exception e)
            {
                e.printStackTrace();
                Log.e("TrpcInpustream",e.getMessage());
            }

        }
        return  redbytes;
    }

    @Override
    public int available() throws IOException {
        if (objlen>-1)
            return objlen;
        try {
            objlen=transport.getobjlength().intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    public void close() throws IOException {
        if (outfilewrter!=null) {
            outfilewrter.flush();
            outfilewrter.close();
            if (remoteoffset>0 && remoteoffset!=available()  )
                new File(_savefile).delete();
        }
        remoteoffset=0;
            transport.close();
    }


    /*@Override
    public synchronized void mark(int readlimit) {

    }


    @Override
    public boolean markSupported() {
        return true;
    }


    @Override
    public synchronized void reset()  {
        //in.reset();
    }


    @Override
    public long skip(long byteCount) {
        return 0l;
    }*/
}
