/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.client.keyvalue.hadoop;

import java.io.IOException;

import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapred.TaskAttemptContext;

import tachyon.TachyonURI;
import tachyon.client.keyvalue.KeyValueStores;
import tachyon.exception.FileDoesNotExistException;
import tachyon.exception.TachyonException;

/**
 * Extension of {@link FileOutputCommitter} where creating, completing, or deleting a
 * {@link KeyValueStores} in different phases of a job's or task's lifecycle is considered.
 * <p>
 * This committer is forced to be used in {@link KeyValueOutputFormat} (no matter what users have
 * set as the {@link org.apache.hadoop.mapred.OutputCommitter} in configration) to merge the
 * key-value stores created by each Reducer into one key-value store under the MapReduce output
 * directory.
 */
class KeyValueOutputCommitter extends FileOutputCommitter {
  private static final KeyValueStores KEY_VALUE_STORES = KeyValueStores.Factory.create();

  private TachyonURI getOutputURI(JobConf conf) {
    return new TachyonURI(FileOutputFormat.getOutputPath(conf).toString());
  }

  private TachyonURI getTaskAttemptOutputURI(TaskAttemptContext context) throws IOException {
    return new TachyonURI(getTaskAttemptPath(context).toString());
  }

  /**
   * {@inheritDoc}
   * <p>
   * Calls {@link FileOutputCommitter#setupJob(JobContext)} first, and then creates an empty
   * key-value store under the job's output directory.
   */
  @Override
  public void setupJob(JobContext context) throws IOException {
    super.setupJob(context);
    try {
      KEY_VALUE_STORES.create(getOutputURI(context.getJobConf())).close();
    } catch (TachyonException e) {
      throw new IOException(e);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes the key-value store created in {@link #setupJob(JobContext)} first, and then
   * calls {@link FileOutputCommitter#abortJob(JobContext, int)}.
   */
  @Override
  public void abortJob(JobContext context, int runState) throws IOException {
    // The output directory should exist since the store writer is just closed.
    try {
      KEY_VALUE_STORES.delete(getOutputURI(context.getJobConf()));
    } catch (TachyonException e) {
      throw new IOException(e);
    }
    super.abortJob(context, runState);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Merges the completed key-value store under the task's temporary output directory to the
   * key-value store created in {@link #setupJob(JobContext)}.
   */
  @Override
  public void commitTask(TaskAttemptContext context) throws IOException {
    try {
      KEY_VALUE_STORES.merge(getTaskAttemptOutputURI(context), getOutputURI(context.getJobConf()));
    } catch (TachyonException e) {
      throw new IOException(e);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Deletes the completed key-value store under the task's temporary output directory, and then
   * calls {@link FileOutputCommitter#abortTask(TaskAttemptContext)}.
   */
  @Override
  public void abortTask(TaskAttemptContext context) throws IOException {
    try {
      KEY_VALUE_STORES.delete(new TachyonURI(getTaskAttemptPath(context).toString()));
    } catch (FileDoesNotExistException e) {
      // The goal of deleting the store is to cleanup directories before aborting the task, since
      // the key-value store directory does not exist, it meets the goal, nothing needs to be done.
    } catch (TachyonException e) {
      throw new IOException(e);
    }
    super.abortTask(context);
  }
}
