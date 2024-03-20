import matplotlib.pyplot as plt
from skimage.io import imread
import numpy as np
import math
import os
import threading
from math import ceil
from keras_contrib.losses import DSSIMObjective
from keras.callbacks import ModelCheckpoint, CSVLogger
from keras.models import Model
from keras.optimizers import Adadelta
from keras.layers import Conv2D, LeakyReLU, Input, AveragePooling2D, Concatenate, Conv2DTranspose
from keras.utils.vis_utils import plot_model

batch_size = 24

def get_image(filename):
    return np.expand_dims(
            np.array(
                imread(filename)[:, :, :3]/255 # Ignore the alpha dimension
            ),
            axis = 0
        )

def concatenate_channels(images):
    return np.concatenate(images, axis = 3)

def create_batch(images):
    return np.concatenate(images, axis = 0)

# get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\WSNormal\\0000000001.png")

import glob
paths = glob.glob("C:\\Users\\emilxp\\Downloads\\conference_images\\Normal\\*.png")
filenames_all = [os.path.basename(path) for path in paths]
filenames_training = filenames_all[:math.floor(len(filenames_all)*0.8)]
filenames_testing = filenames_all[math.floor(len(filenames_all)*0.8):]

def get_image_concatenated(image_name):
    return concatenate_channels(
        (
            get_image(f"C:\\Users\\emilxp\\Downloads\\conference_images\\Normal\\{ image_name }"),
            get_image(f"C:\\Users\\emilxp\\Downloads\\conference_images\\Position\\{ image_name }"),
            get_image(f"C:\\Users\\emilxp\\Downloads\\conference_images\\WSNormal\\{ image_name }"),
        )   
    )

def get_ground_truth(image_name):
    return get_image(f"C:\\Users\\emilxp\\Downloads\\conference_images\\GroundTruth\\{ image_name }")

# for how many iterations to iterate??? Get number of filenames divided by the batch size and that's how many times???
def get_random_filenames(batch_size, filenames):
    return np.random.choice(filenames, batch_size)

# The generator is meant to loop infinitely:
# https://www.pyimagesearch.com/2018/12/24/how-to-use-keras-fit-and-fit_generator-a-hands-on-tutorial/
def batch_generator(batch_size, filenames): # a ako poslednia batch ne e batch_size, a po-malak, kak se handleva???
    number_of_iterations = math.floor(len(filenames)/batch_size)

    while True:
        random_filenames = get_random_filenames(batch_size, filenames) #finagi li vrashta neshto razlichno???
        image_tensors = []
        ground_truth_tensors = []

        for current_filename in random_filenames:
            image_tensors.append(
                get_image_concatenated(current_filename)
            )

            ground_truth_tensors.append(
                get_ground_truth(current_filename)
            )

        yield (
            np.concatenate(image_tensors, axis = 0),
            np.concatenate(ground_truth_tensors, axis = 0)
        )

## Model ##

def input_block(filter_count):
    input_layer = Input(
        batch_shape = (batch_size, 256, 256, 9)
    )

    conv_layer = Conv2D(
        filter_count,    # filter_count
        (3, 3),          # kernel_size
        strides = (1, 1),
        padding = "same",
        data_format = "channels_last",
        kernel_initializer = 'he_normal',
        bias_initializer = 'zeros'
    )(input_layer)

    relu_layer = LeakyReLU(alpha = 0.01)(conv_layer)

    return (input_layer, relu_layer)

def down_block(filter_count, previous_block):
    pool_layer = AveragePooling2D(
        pool_size = (2, 2),
        strides = (2, 2),
        padding = "same",
    )(previous_block)

    conv_layer = Conv2D(
        filter_count,    # filter_count
        (3, 3),          # kernel_size
        strides = (1, 1),
        padding = "same",
        data_format = "channels_last",
        kernel_initializer = 'he_normal',
        bias_initializer = 'zeros'
    )(pool_layer)

    relu_layer = LeakyReLU(alpha = 0.01)(conv_layer)

    return relu_layer

def up_block(filter_count, previous_block, side_block):
    deconv_layer = Conv2DTranspose(
        2*filter_count,
        (4, 4),
        strides = 2,
        padding = "same",
        data_format = "channels_last",
        # output_padding = 1,
        use_bias = False,
        kernel_initializer = "he_normal" # maybe utilize: https://www.programcreek.com/python/example/89672/keras.layers.Conv2DTranspose ???
    )(previous_block)

    concat_layer = Concatenate(
        axis = 3
    )([
        deconv_layer,
        side_block
    ])

    conv_layer = Conv2D(
        filter_count,    # filter_count
        (3, 3),          # kernel_size
        strides = (1, 1),
        padding = "same",
        data_format = "channels_last",
        kernel_initializer = 'he_normal',
        bias_initializer = 'zeros'
    )(concat_layer)

    relu_layer = LeakyReLU(alpha = 0.01)(conv_layer)

    return relu_layer

def output_block(filter_count, previous_block, side_block):
    deconv_layer = Conv2DTranspose(
        2*filter_count,
        (4, 4),
        strides = (2, 2),
        padding = "same",
        data_format = "channels_last",
        # output_padding = (1, 1),
        use_bias = False,
        kernel_initializer = "he_normal" # maybe utilize: https://www.programcreek.com/python/example/89672/keras.layers.Conv2DTranspose ???
    )(previous_block)

    concat_layer = Concatenate(
        axis = 3
    )([
        deconv_layer,
        side_block
    ])

    conv_layer = Conv2D(
        3,               # filter_count(for the RGB channels)
        (3, 3),          # kernel_size
        strides = (1, 1),
        padding = "same",
        data_format = "channels_last",
        kernel_initializer = 'he_normal',
        bias_initializer = 'zeros'
    )(concat_layer)

    relu_layer = LeakyReLU(alpha = 0.01)(conv_layer)

    return relu_layer

(input_layer, down0) = input_block(16)
down1 = down_block(32, down0)
down2 = down_block(64, down1)
down3 = down_block(128, down2)
down4 = down_block(256, down3)
up3 = up_block(128, down4, down3)
up2 = up_block(64, up3,   down2)
up1 = up_block(32, up2,   down1)
up0 = output_block(16, up1,   down0)

output_layer = up0

model = Model(
    inputs = input_layer,
    outputs = output_layer,        
)

# Compile model
# dssim_loss = DSSIMObjective(
#     # Are these value of "k" corresponding to the values of "c" in the deep shading DSSIM definition???
#     k1 = 0.0001,
#     k2 = 0.001,
#     kernel_size = 8,
#     max_value = 1.0
# )

adadelta_optimizer = Adadelta()

model.compile(
    # loss = dssim_loss,
    loss = "mse", # parviat problem se causeva ot dssim loss?!, da razgledam rabotata
    optimizer = adadelta_optimizer,
    metrics = ["accuracy", "mse"],
)

# Print model summary
model.summary()

# Visualize model
plot_model(
    model,
    to_file = 'model_plot.png',
    show_shapes = True,
    show_layer_names = True
)

# Setup saving and loading of weights
model_checkpoint_callback = ModelCheckpoint(
    "weights-{epoch:02d}-{val_loss:.2f}.hdf5",
    monitor = 'val_loss',
    verbose = 1,
    save_weights_only = True,
    period = 0
)

# Setup CSV logging to a file of epoch results
csv_logger = CSVLogger(
    "training-log.csv",
    separator = ',',
    append = True
)

# Train model
training_generator = batch_generator(batch_size, filenames_training)
testing_generator = batch_generator(batch_size, filenames_testing)

model.fit_generator(
    training_generator,
    steps_per_epoch = ceil(len(filenames_training)/batch_size),
    epochs = 10,
    verbose = 2,
    callbacks = [model_checkpoint_callback, csv_logger],
    validation_data = testing_generator,
    validation_steps = ceil(len(filenames_testing)/batch_size),
    # validation_freq = 1,
    # use_multiprocessing = True,
    # workers = 8
)


## DEBUG ##

# generator_instance = batch_generator(16, filenames_training)

# # kakvo stava kogato samia generator e vav kraia??? kak da vidia dali ima next element???
# current_tensor_pair = next(generator_instance)
# print(
#     np.shape(current_tensor_pair[0]),
#     np.shape(current_tensor_pair[1])
# )

# dssim_objective = keras_contrib.losses.DSSIMObjective()
# print(dssim_objective)

# current_tensor_pair = next(generator_instance)
# print(
#     np.shape(current_tensor_pair[0]),
#     np.shape(current_tensor_pair[1])
# )

# current_tensor_pair = next(generator_instance)
# print(
#     np.shape(current_tensor_pair[0]),
#     np.shape(current_tensor_pair[1])
# )

# plt.imshow(current_tensor_pair[0][5, :, :, 3:6], cmap = plt.cm.gray)
# plt.show()



# print(get_random_filenames(3))

# print(get_image_concatenated(get_random_filenames(3)))


# print(filenames)

# print(
#     create_batch(
#         (
#             concatenate_channels(
#                 (
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\Normal\\000000000..png"),
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\Position\\000000000..png"),
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\WSNormal\\000000000..png"),
#                 )   
#             ),
#             concatenate_channels(
#                 (
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\Normal\\0000000001.png"),
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\Position\\0000000001.png"),
#                     get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\WSNormal\\0000000001.png"),
#                 )
#             )
#         )
#     )
# )
    
# img = get_image("C:\\Users\\emilxp\\Downloads\\conference_images\\Normal\\000000000..png")
#print(img)
#plt.imshow(img, cmap=plt.cm.gray)
#plt.show()
