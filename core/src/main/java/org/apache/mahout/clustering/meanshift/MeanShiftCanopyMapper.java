/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.clustering.meanshift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Mapper;

public class MeanShiftCanopyMapper extends Mapper<WritableComparable<?>,MeanShiftCanopy,Text,MeanShiftCanopy> {
  
  private final List<MeanShiftCanopy> canopies = new ArrayList<MeanShiftCanopy>();
  
  private MeanShiftCanopyClusterer clusterer;
  /* (non-Javadoc)
   * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
   */
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    clusterer = new MeanShiftCanopyClusterer(context.getConfiguration());
  }

  /* (non-Javadoc)
   * @see org.apache.hadoop.mapreduce.Mapper#map(java.lang.Object, java.lang.Object, org.apache.hadoop.mapreduce.Mapper.Context)
   */
  @Override
  protected void map(WritableComparable<?> key, MeanShiftCanopy canopy, Context context) throws IOException, InterruptedException {
    clusterer.mergeCanopy(canopy.shallowCopy(), canopies);
  }

  /* (non-Javadoc)
   * @see org.apache.hadoop.mapreduce.Mapper#cleanup(org.apache.hadoop.mapreduce.Mapper.Context)
   */
  @Override
  protected void cleanup(Context context) throws IOException, InterruptedException {
    for (MeanShiftCanopy canopy : canopies) {
      clusterer.shiftToMean(canopy);
      context.write(new Text("canopy"), canopy);
    }
    super.cleanup(context);
  }

}
