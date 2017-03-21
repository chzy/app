package com.nostra13.universalimageloader.core.inputstream;

import android.util.Log;

import com.chd.Transform.InputTrasnport;
import com.chd.Transform.InputTrasnportThum;
import com.chd.Transform.TrpcReadtransport;
import com.chd.proto.FTYPE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lxp1 on 2015/12/13.
 */
public final   class TrpcReadstream extends InputStream {
   private TrpcReadtransport transport;
   private /*RandomAccessFile*/FileOutputStream outfilewrter=null;
   private String _savefile;
    private long remoteoffset=-1;
    private int objlen=-1;
    private final  String TAG="TrpcInpustream";
    private int marked=-1;



    public TrpcReadstream(String name, String savefile, boolean thum) throws Exception   {
        if (thum)
            transport=new InputTrasnportThum(name, FTYPE.PICTURE);
        else
            transport=new InputTrasnport(name,FTYPE.PICTURE);
        if (savefile==null || savefile.indexOf('.')<0)
            Log.d(TAG,"skip save local file");
        _savefile=savefile;
        remoteoffset=0;
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
        if (byteOffset < 0 || byteCount < 0 ||   buffer.length < byteOffset + byteCount) {
            Log.e(TAG, "fun read invalid param");
            return -1;
        }
        if ( objlen<=remoteoffset )
            return -1;
        int redbytes=0;
        Log.i(TAG, Thread.currentThread().getId()+ " offset:" + remoteoffset + " readcount:" + (/*buffer.length - byteOffset*/ byteCount));
        redbytes= transport.read(buffer,byteOffset,remoteoffset,byteCount);

        if (redbytes>-1 )
        {
            if (outfilewrter==null && _savefile!=null) {
                try {
                    outfilewrter = new FileOutputStream(_savefile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("TrpcInputstream", e.getMessage());
                }
            }
            remoteoffset+=redbytes;
            if ( outfilewrter!=null) {
                try {
                    outfilewrter.write(buffer, byteOffset, redbytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TrpcInpustream", e.getMessage());
                }
            }
        }
        return  redbytes;
    }

    @Override
    public int available() throws IOException {
       /* if (objlen>-1)
            return objlen-remoteoffset;
        try {
            objlen=transport.getobjlength().intValue();
            return objlen;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
        return -1;*/
        //return Math.min((int) (objlen-remoteoffset),0);
       return 0;
    }

    public long getSize()  {
        if (objlen>-1)
            return objlen;
        try {
            objlen=transport.getobjlength().intValue();
            return objlen;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
        return 0;
    }




    @Override
    public void close() throws IOException {
        Log.d(TAG,"call Trpcinputstream close()");
        if (outfilewrter!=null) {
            outfilewrter.flush();
            outfilewrter.close();
            if (remoteoffset>0 && remoteoffset!=available()  )
                new File(_savefile).delete();
        }
            //remoteoffset=0;
            transport.close();
            transport=null;
    }


    @Override
    public synchronized void mark(int readlimit) {
        marked=readlimit;
        Log.d(TAG,"mark called");
    }


    @Override
    public boolean markSupported() {
        return true;
    }

       @Override
    public long skip(long byteCount) {
           Log.d(TAG, "skip called: " + byteCount);
           getSize();
           long skip;
           skip=Math.min(Math.max(0, (objlen- remoteoffset+byteCount)),byteCount);
           remoteoffset+=skip;
           return skip;
    }




    @Override
    public synchronized void reset() throws IOException {
        if (marked>0)
            remoteoffset=marked;
        else
            remoteoffset=0;
        Log.d(TAG,"reset called set remoteoffset= "+marked);

    }



}
