package audiocore;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import xyz.gianlu.librespot.player.mixing.output.SinkOutput;

import java.io.IOException;

public class SpotifySinkOutput implements SinkOutput {
    private AudioFrame currentFrame = null;


    @Override
    public void write(final byte[] bytes, final int offset, final int len) throws IOException {
        currentFrame = new Frame();
    }

    @Override
    public void close() throws IOException {

    }

    public AudioFrame getCurrentFrame() {
        return currentFrame;
    }

    private class Frame implements AudioFrame {
        private byte[] currentBuffer;

        public void writeData(final byte[] buffer) {
            currentBuffer = buffer;
        }

        @Override
        public long getTimecode() {
            return 0;
        }

        @Override
        public int getVolume() {
            return 100;
        }

        @Override
        public int getDataLength() {
            return 0;
        }

        @Override
        public byte[] getData() {
            return currentBuffer;
        }

        @Override
        public void getData(byte[] buffer, int offset) {
            System.arraycopy(currentBuffer, 0, buffer, offset, currentBuffer.length);
        }

        @Override
        public AudioDataFormat getFormat() {
            return null;
        }

        @Override
        public boolean isTerminator() {
            return false;
        }
    }
}
